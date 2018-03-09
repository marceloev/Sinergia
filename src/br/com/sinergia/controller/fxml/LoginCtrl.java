package br.com.sinergia.controller.fxml;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.database.conector.DatabaseConf;
import br.com.sinergia.functions.CtrlArquivos;
import br.com.sinergia.functions.MaskField;
import br.com.sinergia.functions.frames.Tela;
import br.com.sinergia.functions.frames.Telas;
import br.com.sinergia.functions.log.ReadRegedit;
import br.com.sinergia.models.statics.AppInfo;
import br.com.sinergia.models.statics.ComputerInfo;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelDialog;
import br.com.sinergia.views.dialogs.ModelDialogButton;
import br.com.sinergia.views.dialogs.ModelException;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static br.com.sinergia.functions.functions.toBoo;

public class LoginCtrl implements Initializable {

    final String keyPath = "HKLM\\Software\\Sinergia\\DBAlias\\Producao\\";
    Boolean capsOff = false;
    int tentativa = 0;
    String versãoExec = "1.0.0";
    DBConn conex;

    @FXML
    private AnchorPane PaneLogin;
    @FXML
    private JFXTextField TxtLogin;
    @FXML
    private JFXPasswordField TxtSenha;
    @FXML
    private Button BtnAcessar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AppInfo.setVersaoExec(versãoExec);
        propriedades();
        registraInfo();
    }

    /*
    |----------------------------------------------------------------------------------------------------------|
    | O JavaFX não está conseguindo pegar o Locking state do CapsLock nativamente, temos que aplicar contornos.|
    |----------------------------------------------------------------------------------------------------------|
     */

    private void propriedades() {
        MaskField.MaxUpperCharField(TxtLogin, 20);
        MaskField.MaxCharField(TxtSenha, 20);
        TxtLogin.setOnAction((ActionEvent evt) -> {
            autenticarLogin();
        });
        TxtSenha.setOnAction((ActionEvent evt) -> {
            autenticarLogin();
        });
        BtnAcessar.setOnAction((ActionEvent evt) -> {
            autenticarLogin();
        });
        TxtSenha.focusedProperty().addListener((obs, wasF, isF) -> {
            if (isF) {
                Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, false);
                capsOff = true;
            }
        });
        TxtSenha.setOnKeyReleased(evt -> {
            if (evt.getCode() == KeyCode.CAPS) {
                capsOff = !capsOff;
                Platform.runLater(() -> {
                    if (capsOff) TxtSenha.setFocusColor(Color.valueOf("#2b55dd"));
                    else TxtSenha.setFocusColor(Color.RED);
                });
            }
        });
    }

    private Boolean validLogin() {
        try {
            if (TxtLogin.getText() == null || TxtLogin.getText().equals("")) {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null, "Login não pode ser vazio"));
                ModelDialog.getDialog().raise();
                return false;
            } else if (TxtSenha.getText() == null || TxtSenha.getText().equals("")) {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null, "Senha não pode ser vazia"));
                ModelDialog.getDialog().raise();
                return false;
            }
            if (tryConectDB()) {
                conex = new DBConn(this.getClass(), true, "SELECT COUNT(1) FROM TSIUSU WHERE LOGIN = ?");
                conex.addParameter(TxtLogin.getText());
                conex.createSet();
                conex.rs.next();
                Boolean UserExists = toBoo(conex.rs.getInt(1));
                if (!UserExists) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Usuário não encontrado para login digitado"));
                    ModelDialog.getDialog().raise();
                    return false;
                } //Se continuou, é porque o usuário existe.
                conex = new DBConn(this.getClass(), false, "SELECT USU.ATIVO, USU.CODUSU, USU.NOME, USU.LOGIN, USU.FOTO, USU.PERFIL, USU.SENHA, MD5(?) AS CRYPT,\n" +
                        "GET_TSIPAR_T(?, ?) AS VERSAOATUALDB, EMP.CODEMP, EMP.RAZAOSOCIAL, EMP.NOMEFANTASIA, EMP.CNPJ " +
                        "FROM TSIUSU USU\n" +
                        "INNER JOIN TSIEMP EMP\n" +
                        "ON (USU.CODEMP = EMP.CODEMP)" +
                        "WHERE USU.LOGIN = ?");
                conex.addParameter(TxtSenha.getText(), "(Criptografado)");
                conex.addParameter("VERSAOATUALDB");
                conex.addParameter(0);
                conex.addParameter(TxtLogin.getText());
                conex.createSet();
                conex.rs.next();
                AppInfo.setVersaoDB(conex.rs.getString("VERSAOATUALDB"));
                Image ImgUsu;
                if (conex.rs.getBytes("FOTO") != null) {
                    InputStream input = new ByteArrayInputStream(conex.rs.getBytes("FOTO"));
                    ImgUsu = new Image(input);
                } else {
                    ImgUsu = new Image("/br/com/sinergia/properties/images/default.png");
                }
                User.setCurrent(new User(conex.rs.getInt("CODUSU"),
                        toBoo(conex.rs.getString("ATIVO")),
                        TxtLogin.getText(),
                        conex.rs.getString("NOME"),
                        ImgUsu,
                        conex.rs.getInt("PERFIL"),
                        conex.rs.getString("SENHA"),
                        conex.rs.getString("CRYPT"),
                        conex.rs.getInt("CODEMP"),
                        conex.rs.getString("NOMEFANTASIA"),
                        conex.rs.getString("RAZAOSOCIAL"),
                        conex.rs.getString("CNPJ")));
            }
            return true;
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar comunicar com Banco de Dados\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
            return false;
        } finally {
            conex.desconecta();
        }
    }

    private Boolean tryConectDB() {
        String ip = ReadRegedit.readRegistry(keyPath, "IP");
        String port = ReadRegedit.readRegistry(keyPath, "Port");
        String user = ReadRegedit.readRegistry(keyPath, "User");
        String password = uncryptPass(ReadRegedit.readRegistry(keyPath, "Password"));
        try {
            DatabaseConf.setDatabaseConf(ip, port, user, password);
            DBConn conex = new DBConn(this.getClass(), false, 2,
                    "SELECT 1 FROM DUAL");
            conex.createSet();
            return true;
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar estabelecer conexão com banco de dados\n" + ex + "\n" +
                    "Caminho: jdbc:oracle:thin:@" + ip + ":" + port + "\n" +
                    "Usuário: " + user + "\n" +
                    "Senha: (Criptografado)", ex));
            ModelException.getDialog().raise();
            System.out.println("False");
            return false;
        }
    }

    private void autenticarLogin() {
        if (validLogin()) {
            try {
                if (tentativa < 4) tentativa++;
                if (tentativa == 4) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                            null,
                            "Tentativa de login excedida. Máquina bloqueada."));
                    ModelDialog.getDialog().raise();
                    return;
                }
                if (User.getCurrent().getAtivo() == false) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                            null,
                            "Usuário não está ativo, não pode acessar o sistema"));
                    ModelDialog.getDialog().raise();
                    return;
                }
                if (!User.getCurrent().getSenhaUsu().equals(User.getCurrent().getCryptSenha())) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                            null,
                            "Senha digitada inválida"));
                    ModelDialog.getDialog().raise();
                    return;
                }
                registraSessao();
                Tela telaPrincipal = Telas.getByCod(1);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(telaPrincipal.getFounder()));
                Parent root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(telaPrincipal.getDescrTela() + "(" + User.getCurrent().getCodUsu() + " - " +
                        User.getCurrent().getLoginUsu() + ")");
                stage.getIcons().add(new Image("/br/com/sinergia/properties/images/Icone_Sistema.png"));
                stage.show();
                stage.setOnCloseRequest(e -> {
                    ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(),
                            null,
                            "Deseja realmente sair do sistema?"));
                    ButtonType[] Btns = new ButtonType[2];
                    Btns[0] = new ButtonType("Sim");
                    Btns[1] = new ButtonType("Não");
                    ModelDialogButton.getDialogButton().createButton(Btns);
                    if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[1]) { //Não sair
                        e.consume();
                    } else {
                        String arqFecha = null;
                        if (AppInfo.getMainTabPane().getTabs().size() > 1) {
                            arqFecha = CtrlArquivos.busca(User.getCurrent().getCodUsu(), "Finalizar com telas pendetes");
                            if (arqFecha == null || arqFecha.equals("P")) { //Não existe registro ainda ou perguntar
                                ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(),
                                        null,
                                        "O sistema detectou que existem telas abertas\n" +
                                                "Caso algumas dessas telas estejam com alteração pendente, as alterações serão desfeitas\n" +
                                                "Dejesa realmente sair ou revisar as telas?"));
                                CheckBox ckb = new CheckBox("Não perguntar novamente?");
                                ModelDialogButton.getDialogButton().addCheckBox(ckb);
                                Btns[0] = new ButtonType("Sair");
                                Btns[1] = new ButtonType("Revisar");
                                ModelDialogButton.getDialogButton().createButton(Btns);
                                if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[0]) {
                                    if (ckb.isSelected()) {
                                        CtrlArquivos.registra(User.getCurrent().getCodUsu(), "Finalizar com telas pendentes", "S");
                                    }
                                    User.getCurrent().closeSessao();
                                } else {
                                    if (ckb.isSelected()) {
                                        CtrlArquivos.registra(User.getCurrent().getCodUsu(), "Finalizar com telas pendentes", "N");
                                    }
                                    e.consume();
                                }
                            }
                        } else {
                            if (arqFecha.equals("S")) {
                                User.getCurrent().closeSessao();
                            } else if (arqFecha.equals("N")) {
                                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING,
                                        this.getClass(),
                                        null,
                                        "Existem telas ainda abertas, finalize-as primeiro"));
                                ModelDialog.getDialog().raise();
                                e.consume();
                            } else {
                                System.err.println("arqFecha not programmed: " + arqFecha);
                            }
                        }
                    }
                });
                Stage stage_old = (Stage) TxtLogin.getScene().getWindow();
                stage_old.close();
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null , ex.getMessage(), ex));
                ModelException.getDialog().raise();
            }
        }

    }

    private String uncryptPass(String StringByted) {
        ArrayList<String> ArrayCryptPass = new ArrayList<>(Arrays.asList(StringByted.split("-")));
        String DBPassword = "";
        for (String CryptPass : ArrayCryptPass) {
            CryptPass = CryptPass.trim();
            char UnByted = (char) Integer.parseInt(CryptPass, 2);
            DBPassword = DBPassword + UnByted;
        }
        return DBPassword;
    }

    private void registraSessao() throws Exception {
        try {
            conex = new DBConn(this.getClass(), false, "SELECT GET_CODSESSAO(?) AS SESSAO FROM DUAL");
            conex.addParameter(User.getCurrent().getCodUsu());
            conex.createSet();
            conex.rs.next();
            User.getCurrent().setCodSessão(conex.rs.getInt(1));
            System.out.println("Passou");
            conex = new DBConn(this.getClass(), false,
                    "INSERT INTO TSISES\n"
                            + "(CODSESSAO, CODUSU, DHLOGIN, IPMAQ, NOMEMAQ, VERSAOEXEC)\n"
                            + "VALUES\n"
                            + "(?, ?, SYSDATE, ?, ?, ?)");
            System.out.println("Passou2");
            conex.addParameter(User.getCurrent().getCodSessão());
            conex.addParameter(User.getCurrent().getCodUsu());
            conex.addParameter(ComputerInfo.getIPMáquina());
            conex.addParameter(ComputerInfo.getNomeMáquina());
            conex.addParameter(AppInfo.getVersaoExec());
            conex.run();
        } catch (Exception ex) {
            throw new Exception("Erro ao tentar registrar sessão\n" + ex.getMessage());
        }
    }

    private void registraInfo() throws Error {
        try {
            ComputerInfo.setIPMáquina(InetAddress.getLocalHost().getHostAddress());
            ComputerInfo.setNomeMáquina(InetAddress.getLocalHost().getHostName());
            ComputerInfo.setVersãoExec(versãoExec);
        } catch (Exception ex) {
            throw new Error("Erro ao tentar obter informações da máquina\n" +
                    "Por favor, contate o suporte.\n" + ex, ex);
        }
    }

    private void getCaseSensitive(Boolean isCapsOn, Boolean isFocused) {
        if (isFocused && isCapsOn) {
            TxtSenha.setStyle("-fx-border-color: OrangeRed;");
            TxtSenha.setTooltip(new Tooltip("CapsLock Ativado"));
        } else {
            TxtSenha.setStyle("-fx-border-color: null;");
            TxtSenha.setTooltip(null);
        }
    }

}