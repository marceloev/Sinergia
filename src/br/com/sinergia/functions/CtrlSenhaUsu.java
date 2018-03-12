package br.com.sinergia.functions;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelDialog;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class CtrlSenhaUsu {

    private int codUsu;
    private String senha;
    private Boolean enableEmpty;

    public CtrlSenhaUsu(int codUsu, String senha) {
        super();
        setCodUsu(codUsu);
        setSenha(senha);
        setEnableEmpty(true);
        callFrame(true);
    }

    public CtrlSenhaUsu(int codUsu, String senha, Boolean enableEmpty) {
        super();
        setCodUsu(codUsu);
        setSenha(senha);
        setEnableEmpty(enableEmpty);
        callFrame(true);
    }

    public Boolean autenticate() {
        try {
            Dialog<String> dialogConfSenha = new Dialog<>();
            dialogConfSenha.setTitle("Sistema Sinergia:");
            dialogConfSenha.setHeaderText("Alterar senha do usuário");
            Stage stage = (Stage) dialogConfSenha.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("/br/com/sinergia/views/images/Icone_Sistema.png"));
            ButtonType btnOK = new ButtonType("Confirmar", ButtonBar.ButtonData.APPLY);
            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(10, 10, 10, 10));
            PasswordField txtSenha = new PasswordField();
            txtSenha.setPromptText("Senha do Usuário");
            gridPane.add(new Label("Confirme suas credenciais para alterar senha:"), 0, 1);
            gridPane.add(txtSenha, 0, 2);
            dialogConfSenha.getDialogPane().getButtonTypes().add(btnOK);
            dialogConfSenha.getDialogPane().setContent(gridPane);
            txtSenha.requestFocus();
            dialogConfSenha.setResultConverter(dialogButton -> {
                if (dialogButton == btnOK) {
                    return new String(txtSenha.getText());
                }
                return null;
            });
            Optional<String> senha = dialogConfSenha.showAndWait();
            senha.ifPresent(e -> {
                if (!e.equals(getSenha())) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Senha digitada inválida. Tente novamente."));
                    ModelDialog.getDialog().raise();
                    autenticate();
                }
            });
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar chamar método de atualização de senha\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            return true;
        }
    }

    public void callFrame(Boolean needsCrypt) {
        Boolean autentified = false;
        if (needsCrypt) autentified = autenticate();
        else autentified = true;
        if (autentified) {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Sistema Sinergia:");
            dialog.setHeaderText("Alterar senha do usuário");
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("/br/com/sinergia/views/images/Icone_Sistema.png"));
            dialog.setGraphic(new ImageView("/br/com/sinergia/views/images/Icone_Senha.png"));
            ButtonType btnOk = new ButtonType("Confirmar", ButtonBar.ButtonData.APPLY);
            Node okButton = dialog.getDialogPane().lookupButton(btnOk);
            dialog.getDialogPane().getButtonTypes().add(btnOk);
            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(10, 5, 10, 5));
            PasswordField txtSenha = new PasswordField();
            txtSenha.setPromptText("Digite aqui sua nova senha");
            PasswordField txtSenha2 = new PasswordField();
            txtSenha2.setPromptText("Confirme sua nova senha");
            gridPane.add(new Label("Nova senha: "), 2, 1);
            gridPane.add(txtSenha, 2, 2);
            gridPane.add(new Label("Confirmação de nova senha: "), 2, 3);
            gridPane.add(txtSenha2, 2, 4);
            dialog.getDialogPane().setContent(gridPane);
            txtSenha.requestFocus();
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == btnOk) {
                    return new Pair<String, String>(txtSenha.getText(), txtSenha2.getText());
                }
                return null;
            });
            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(password -> {
                if (password.getKey().equals(password.getValue())) {
                    if (functions.nvl(password.getKey()).equals("")) {
                        ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                                "Nova senha não pode ser vazia"));
                        ModelDialog.getDialog().raise();
                        callFrame(false);
                    } else {
                        if (updateUserPass(getCodUsu(), password.getValue(), true)) {
                            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(), null,
                                    "Senha atualizada com sucesso."));
                            ModelDialog.getDialog().raise();
                        }
                    }
                } else {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Senha e Confirmação de senha divergem"));
                    ModelDialog.getDialog().raise();
                    callFrame(false);
                }
            });
        }
    }

    public Boolean updateUserPass(int codUsu, String senha, Boolean save) {
        DBConn conex = null;
        try {
            if (save) {
                conex = new DBConn(CtrlSenhaUsu.class, true,
                        "SELECT MD5(?) FROM DUAL");
                conex.addParameter(senha);
                conex.createSet();
                conex.rs.next();
                User.getCurrent().setCryptSenha(conex.rs.getString(1));
                User.getCurrent().setSenhaUsu(senha);
            }
            conex = new DBConn(CtrlSenhaUsu.class, false,
                    "UPDATE TSIUSU SET SENHA = MD5(?) WHERE CODUSU = ?");
            conex.addParameter(senha);
            conex.addParameter(codUsu);
            conex.run();
            return true;
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(CtrlSenhaUsu.class, null,
                    "Erro ao tentar atualizar senha\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
            return false;
        } finally {
            conex.desconecta();
        }
    }

    public int getCodUsu() {
        return codUsu;
    }

    public void setCodUsu(int codUsu) {
        this.codUsu = codUsu;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getEnableEmpty() {
        return enableEmpty;
    }

    public void setEnableEmpty(Boolean enableEmpty) {
        this.enableEmpty = enableEmpty;
    }
}
