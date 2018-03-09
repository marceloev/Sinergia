package br.com.sinergia.models.extendeds;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.functions.CtrlArquivos;
import br.com.sinergia.functions.functions;
import br.com.sinergia.models.statics.AppInfo;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;

public class ButtonTela extends Button {

    private int codTela;
    private String descrTela;
    private String path;
    private Boolean frame;
    private String founder;

    public ButtonTela(int codTela, String descrTela, String path, Boolean frame, String founder) {
        super();
        setCodTela(codTela);
        setDescrTela(descrTela);
        setPath(path);
        setFrame(frame);
        setFounder(founder);
        Platform.runLater(() -> {
            addEvents();
        });
    }

    public void addEvents() {
        try {
            this.setOnAction(action -> {
                if (AppInfo.getTabPanes().contains(this.getDescrTela())) {
                    AppInfo.getMainTabPane().getSelectionModel().select(
                            AppInfo.getTabPanes().indexOf(this.getDescrTela()) + 1); // Para expandir a Aba
                } else {
                    try {
                        gravaRegistro(this.getDescrTela());
                        Tab tela = new Tab(this.getDescrTela());
                        tela.setTooltip(new Tooltip(this.getPath() + " > " + this.getDescrTela()));
                        tela.setOnCloseRequest(close -> {
                            AppInfo.getTabPanes().remove(this.getDescrTela());
                        });
                        FXMLLoader FXMLloader = new FXMLLoader(getClass().getResource(this.getFounder()));
                        Parent frame = FXMLloader.load();
                        tela.setContent(frame);
                        AppInfo.getMainTabPane().getTabs().add(tela);
                        AppInfo.getTabPanes().add(this.getDescrTela());
                        AppInfo.getMainTabPane().getSelectionModel().select(AppInfo.getTabPanes().indexOf(this.getDescrTela()) + 1);
                        functions.setLayoutPane((Pane) tela.getContent());
                    } catch (Exception ex) {
                        ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar exibir tela: " +
                                this.getDescrTela() + "\n" + ex.getMessage(), ex));
                        ModelException.getDialog().raise();
                    }
                }
            });
        } catch (Exception ex) {
            //Não precisa tratar o TabAbertas pois, ele add, se houver, substitui.
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar exibir tela: " + this.getDescrTela() + "\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        }
        try {
            this.setOnMouseClicked(mouse -> {
                if (mouse.getButton() == mouse.getButton().SECONDARY) {
                    if (AppInfo.getStrTelasFav().contains(this.getDescrTela())) { //Já existe, então remove
                        AppInfo.getStrTelasFav().remove(this.getDescrTela());
                        AppInfo.getvBoxFavoritos().getChildren().remove(this);
                    } else {
                        AppInfo.getStrTelasFav().add(this.getDescrTela());
                        AppInfo.getvBoxFavoritos().getChildren().add(this);
                    }
                    //Atentar se não está saindo com [], ex: [Locais, Produtos], causando erro.
                    CtrlArquivos.registra(User.getCurrent().getCodUsu(), "Telas Favoritas", AppInfo.getStrTelasFav().toString());
                }
            });
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar registrar favoritar tela: " + this.getDescrTela() + "\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        }
    }

    private void gravaRegistro(String tela) {
        DBConn conex;
        try {
            conex = new DBConn(this.getClass(),
                    "INSERT INTO TSIREG\n" +
                            "(CODREG, CODUSU, CODSESSAO, DHACESSO, TELA)\n" +
                            "VALUES\n" +
                            "(GET_CODREG(?), ?, ?, ?, ?)");
            conex.addParameter(User.getCurrent().getCodUsu());
            conex.addParameter(User.getCurrent().getCodUsu());
            conex.addParameter(User.getCurrent().getCodSessão());
            conex.addParameter(java.sql.Timestamp.from(java.time.Instant.now()));
            conex.addParameter(tela);
            conex.run();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar gravar registro: " + tela + "\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        }
    }

    public int getCodTela() {
        return codTela;
    }

    public void setCodTela(int codTela) {
        this.codTela = codTela;
    }

    public String getDescrTela() {
        return descrTela;
    }

    public void setDescrTela(String descrTela) {
        this.descrTela = descrTela;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean isFrame() {
        return frame;
    }

    public void setFrame(Boolean frame) {
        this.frame = frame;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }
}
