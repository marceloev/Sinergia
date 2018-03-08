package br.com.sinergia.controller.fxml;

import br.com.sinergia.controller.statics.AppInfo;
import br.com.sinergia.controller.statics.ComputerInfo;
import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.functions.MaskField;
import br.com.sinergia.functions.log.ReadRegedit;
import br.com.sinergia.models.statics.AppInfo;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelDialog;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sun.security.pkcs11.wrapper.Functions;

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

public class LoginController implements Initializable {

    final String keyPath = "HKLM\\Software\\Sinergia\\DBAlias\\Produção\\";
    String versãoExec = "1.0.0";
    DBConn conex;

    @FXML
    private AnchorPane PaneLogin;
    @FXML
    private TextField TxtLogin, TxtSenha;
    @FXML
    private Button BtnAcessar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        PaneLogin.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CAPS) {
                isCapsLocked = !isCapsLocked;
                getCaseSensitive(isCapsLocked, TxtSenha.isFocused());
            }
        });
        TxtSenha.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, isCapsLocked);
                getCaseSensitive(isCapsLocked, true);
            } else {
                getCaseSensitive(isCapsLocked, false);
            }
        });
    }

    private void catchInfoLogin() {
        try {
            if (TxtLogin.getText() == null || TxtLogin.getText().equals("")) {
                throw new Error("Login não pode ser vazio");
            } else if (TxtSenha.getText() == null || TxtSenha.getText().equals("")) {
                throw new Error("Senha não pode ser vazia");
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
                    return;
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
        } catch (Error ex) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null, ex.getMessage()));
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
        }
    }

    private Boolean tryConectDB() throws Error, SQLException {
        String ip = ReadRegedit.readRegistry(keyPath, "IP");
        String port = ReadRegedit.readRegistry(keyPath, "Port");
        String user = ReadRegedit.readRegistry(keyPath, "User");
        String password = ReadRegedit.readRegistry(keyPath, "Password");
        try {
            DBConn conex = new DBConn(this.getClass(), true, 2,
                    "SELECT 1 FROM DUAL");
            conex.createSet();
            return true;
        } catch (SQLException ex) {
            throw new SQLException("Erro ao tentar estabelecer conexão com banco de dados\n" + ex + "\n" +
                    "Caminho: jdbc:oracle:thin:@" + ip + ":" + port + "\n" +
                    "Usuário: " + user + "\n" +
                    "Senha: (Criptografado)", ex);
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

    private void autenticarLogin() {
        try {
            catchInfoLogin();
            if (noError) {
                if (tentativa < 4) {
                    tentativa = tentativa + 1;
                }
                if (tentativa == 4) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                            null,
                            "Tentativa de login excedida. Máquina bloqueada."));
                    ModelDialog.getDialog().raise();
                    return;
                } else if (User.getCurrent().getAtivo() == false) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                            null,
                            "Usuário não está ativo, não pode acessar o sistema"));
                    ModelDialog.getDialog().raise();
                    return;
                } else if (!User.getCurrent().getSenhaUsu().equals(User.getCurrent().getCryptSenha())) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                            null,
                            "Senha digitada inválida"));
                    ModelDialog.getDialog().raise();
                    return;
                } else {
                    try {
                        RegistraSessão();
                        if (noError) {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/br/com/sinergia/views/Principal.fxml"));
                            Parent root1 = (Parent) fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root1));
                            stage.setTitle("Sistema Sinergia ( " + User.getCurrent().getCódUsu() + " - " + User.getCurrent().getLoginUsu() + " )");
                            stage.getIcons().add(new Image("/br/com/sinergia/properties/images/Icone_Sistema.png"));
                            stage.setMaximized(true);
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
                                    if (AppObjects.getAppObjects().getAbaPane().getTabs().size() > 1) { //Sair
                                        String arqFecha = ctrlArquivos.Busca(User.getCurrent().getCódUsu(), "Finalizar com telas pendentes");
                                        if (arqFecha.equals("0") || arqFecha.equals("P")) {//Não existe registro ainda ou perguntar
                                            ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(),
                                                    null,
                                                    "O sistema detectou que existem telas abertas\n" +
                                                            "Caso algumas dessas telas estejam com alteração pendente, as alterações serão desfeitas\n" +
                                                            "Dejesa realmente sair ou revisar as telas?"));
                                            CheckBox Ckb = new CheckBox("Não perguntar novamente?");
                                            Ckb.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                                            ModelDialogButton.getDialogButton().addCheckBox(Ckb);
                                            Btns[0] = new ButtonType("Sair");
                                            Btns[1] = new ButtonType("Revisar");
                                            ModelDialogButton.getDialogButton().createButton(Btns);
                                            if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[0]) {
                                                if (Ckb.isSelected()) {
                                                    ctrlArquivos.Registra(User.getCurrent().getCódUsu(), "Finalizar com telas pendentes", "S");
                                                }
                                                User.getCurrent().closeSessão();
                                            } else {
                                                if (Ckb.isSelected()) {
                                                    ctrlArquivos.Registra(User.getCurrent().getCódUsu(), "Finalizar com telas pendentes", "N");
                                                }
                                                e.consume();
                                            }
                                        } else {
                                            if (arqFecha.equals("S")) {
                                                //Apenas fecha
                                                User.getCurrent().closeSessão();
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
                                    } else {
                                        User.getCurrent().closeSessão();
                                    }
                                }
                            });
                            Stage stage_old = (Stage) TxtLogin.getScene().getWindow();
                            stage_old.close();
                        }
                    } catch (Exception ex) {
                        noError = false;
                        ModelException Error = new ModelException(this.getClass(), null, "Erro ao tentar inicializar tela principal\n"
                                + ex.getMessage(), ex);
                        Error.raise();
                    }
                }
            }
        }

        private String uncryptPass (String StringByted){
            ArrayList<String> ArrayCryptPass = new ArrayList<String>(Arrays.asList(StringByted.split("-")));
            String DBPassword = "";
            for (String CryptPass : ArrayCryptPass) {
                CryptPass = CryptPass.trim();
                char UnByted = (char) Integer.parseInt(CryptPass, 2);
                DBPassword = DBPassword + UnByted;
            }
            return DBPassword;
        }

        private void RegistraSessão () {
            try {
                statement = new Statement("SELECT GET_CODSESSAO(?) AS SESSAO FROM DUAL");
                statement.addParameter(User.getCurrent().getCódUsu());
                statement.createSet();
                statement.rs.next();
                User.getCurrent().setCódSessão(statement.rs.getInt("SESSAO"));
                statement = new Statement("INSERT INTO TSISES\n"
                        + "(CODSESSAO, CODUSU, DHLOGIN, IPMAQ, NOMEMAQ, VERSAOEXEC)\n"
                        + "VALUES\n"
                        + "(?, ?, ?, ?, ?, ?)");
                statement.addParameter(User.getCurrent().getCódSessão());
                statement.addParameter(User.getCurrent().getCódUsu());
                statement.addParameter(java.sql.Timestamp.from(java.time.Instant.now()));
                statement.addParameter(AppInfo.getInfo().getIPMáquina());
                statement.addParameter(AppInfo.getInfo().getNomeMáquina());
                statement.addParameter(AppInfo.getInfo().getVersãoExec());
                statement.run();
            } catch (SQLException ex) {
                noError = false;
                ModelException.setNewException(new ModelException(this.getClass(), null,
                        "Erro ao tentar registrar sessão\n" + ex.getMessage() + "\nA aplicação será finalizada.", ex));
                ModelException.getException().raise();
                System.exit(0);
            } catch (Exception ex) {
                noError = false;
                ModelException.setNewException(new ModelException(this.getClass(), null,
                        "Erro ao tentar registrar sessão\n" + ex.getMessage() + "\nA aplicação será finalizada.", ex));
                ModelException.getException().raise();
                System.exit(0);
            }
        }

        private void getCaseSensitive (Boolean isCapsOn, Boolean isFocused){
            if (isFocused && isCapsOn) {
                TxtSenha.setStyle("-fx-border-color: OrangeRed;");
                TxtSenha.setTooltip(new Tooltip("CapsLock Ativado"));
            } else {
                TxtSenha.setStyle("-fx-border-color: null;");
                TxtSenha.setTooltip(null);
            }
        }

    }
}