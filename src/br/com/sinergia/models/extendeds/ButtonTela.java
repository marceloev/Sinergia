package br.com.sinergia.models.extendeds;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.functions.CtrlAccMenu;
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
        this.setText(descrTela);
        setCodTela(codTela);
        setDescrTela(descrTela);
        setPath(path);
        setFrame(frame);
        setFounder(founder);
        this.setStyle("-fx-border-color: #696969;" +
                "-fx-max-width: Infinity;" +
                "-fx-pref-height: 35;" + //Por algum erro do CSS, se você trocar PREF por MAX, ele não atualiza
                "-fx-border-color: Silver");
        Platform.runLater(() -> {
            addEvents();
            if (CtrlAccMenu.favArquivoIni != null && CtrlAccMenu.favArquivoIni.contains(this.getDescrTela())) {
                setFavoriteEvent(true);
            }
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
                    setFavoriteEvent(false);
                }
            });
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar registrar favoritar tela: " + this.getDescrTela() + "\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        }
    }

    private void setFavoriteEvent(Boolean init) {
        if (init) {
            if (CtrlAccMenu.favArquivoIni.contains(this.getDescrTela()) &&
                    !AppInfo.getStrTelasFav().contains(this.getDescrTela())) {
                AppInfo.getStrTelasFav().add(this.getDescrTela());
                AppInfo.getvBoxFavoritos().getChildren().add(new ButtonTela(this.getCodTela(), this.getDescrTela(), this.getPath(), this.isFrame(), this.getFounder()));
            }
        } else {
            if (AppInfo.getStrTelasFav().contains(this.getDescrTela())) {
                AppInfo.getvBoxFavoritos().getChildren().remove(AppInfo.getvBoxFavoritos().getChildren().get(AppInfo.getStrTelasFav().indexOf(this.getDescrTela())));
                AppInfo.getStrTelasFav().remove(this.getDescrTela());
            } else {
                AppInfo.getvBoxFavoritos().getChildren().add(new ButtonTela(this.getCodTela(), this.getDescrTela(), this.getPath(), this.isFrame(), this.getFounder()));
                AppInfo.getStrTelasFav().add(this.getDescrTela());
            }
            CtrlArquivos.registra(User.getCurrent().getCodUsu(), "Telas Favoritas", AppInfo.getStrTelasFav());
        }
    }

    private void gravaRegistro(String tela) {
        DBConn conex = null;
        try {
            conex = new DBConn(this.getClass(),
                    "INSERT INTO TSIREG\n" +
                            "(CODREG, CODUSU, CODSESSAO, DHACESSO, TELA)\n" +
                            "VALUES\n" +
                            "(GET_CODREG(?), ?, ?, SYSDATE, ?)");
            conex.addParameter(User.getCurrent().getCodUsu());
            conex.addParameter(User.getCurrent().getCodUsu());
            conex.addParameter(User.getCurrent().getCodSessão());
            conex.addParameter(tela);
            conex.run();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar gravar registro: " + tela + "\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
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

