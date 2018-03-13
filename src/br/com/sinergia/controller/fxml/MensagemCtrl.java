package br.com.sinergia.controller.fxml;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.database.querys.ListaQuerys;
import br.com.sinergia.functions.MaskField;
import br.com.sinergia.models.statics.AppInfo;
import br.com.sinergia.models.usage.Mensagem;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelDialog;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static br.com.sinergia.functions.functions.*;

public class MensagemCtrl implements Initializable {

    DBConn conex;

    @FXML
    private TextField TxtCodRemetente, TxtDescrRemetente, TxtTitulo;
    @FXML
    private TextArea TxtMensagem;
    @FXML
    private Button BtnEnviar;
    @FXML
    private ToggleButton TggDestinatarios;
    @FXML
    private Spinner<Integer> SpnPrioridade;
    @FXML
    private ImageView imgAdicionaDest, imgRemoveDest;
    @FXML
    private ListView<Pair<Integer, String>> ListDestinatarios;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
    }

    private void estrutura() {
        Stage stage = (Stage) TxtCodRemetente.getScene().getWindow();
        stage.initOwner(AppInfo.getStageMain());
        TxtCodRemetente.setText(User.getCurrent().getCodUsu() + "");
        TxtDescrRemetente.setText(User.getCurrent().getLoginUsu() + " - " + User.getCurrent().getNomeUsu());
        MaskField.SpnFieldCtrl(SpnPrioridade, 1, 3);
        SpnPrioridade.getValueFactory().setValue(3);
        TggDestinatarios.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                imgAdicionaDest.setDisable(true);
                imgRemoveDest.setDisable(true);
                ListDestinatarios.setDisable(true);
            } else {
                imgAdicionaDest.setDisable(false);
                imgRemoveDest.setDisable(false);
                ListDestinatarios.setDisable(false);
            }
        });
        BtnEnviar.setOnAction(e -> {
            sendMessage(
                    new Mensagem(
                            SpnPrioridade.getValueFactory().getValue(),
                            TxtTitulo.getText(),
                            TxtMensagem.getText()));
        });
    }

    public void ctrlDestinatario(Boolean adding, Pair<Integer, String> destinario) {
        long qtd = ListDestinatarios.getItems().stream().filter(destList -> destList.getKey().equals(destinario.getKey())).count();
        if (adding) {
            if (qtd > 0) {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                        "Usuário: " + destinario.getKey() + " - " + destinario.getValue() + "\n" +
                                "Já existe na lista de destinários."));
                ModelDialog.getDialog().raise();
            } else {
                ListDestinatarios.getItems().add(destinario);
            }
        } else {
            if (qtd == 0) {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                        "Usuário: " + destinario.getKey() + " - " + destinario.getValue() + "\n" +
                                "Não consta na lista de destinários."));
                ModelDialog.getDialog().raise();
            } else {
                ListDestinatarios.getItems().remove(ListDestinatarios.getItems().stream().filter(destList -> destList.getKey().equals(destinario.getKey())));
            }
        }

    }

    private void sendMessage(Mensagem mensagem) {
        if (validMensagem()) {
            try {
                List<String> listUsuDest = new ArrayList<>();
                if (TggDestinatarios.isSelected()) {
                    conex = new DBConn(this.getClass(), null, ListaQuerys.getAgrListCodUsu());
                    conex.createSet();
                    conex.rs.next();
                    listUsuDest = Arrays.asList(conex.rs.getString(1).split(";"));
                } else {
                    List<String> finalListUsuDest = listUsuDest;
                    ListDestinatarios.getItems().forEach(destinatario -> {
                        finalListUsuDest.add(destinatario.getKey().toString());
                    });
                }
                for (String usuDest : listUsuDest) {
                    conex = new DBConn(this.getClass(), false, ListaQuerys.getQueryInsertLembrete());
                    conex.addParameter(usuDest);
                    conex.addParameter("N");
                    conex.addParameter(mensagem.getMensagem());
                    conex.addParameter(mensagem.getPrioridade());
                    conex.addParameter(mensagem.getDhAlter());
                    conex.addParameter(mensagem.getTitulo());
                    conex.addParameter(null);
                    conex.addParameter(User.getCurrent().getCodUsu());
                    conex.run();
                }
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(), null,
                        "Mensagem enviada com sucesso!"));
                ModelDialog.getDialog().raise();
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null,
                        "Erro ao tentar enviar mensagens aos destinatários\n" + ex.getMessage(), ex));
                ModelException.getDialog().raise();
            } finally {
                conex.desconecta();
            }
        }
    }

    private Boolean validMensagem() {
        if (ListDestinatarios.getItems().isEmpty() && !TggDestinatarios.isSelected()) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Informe ao menos um remetente para envio da mensagem"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (nvl(TxtTitulo.getText()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Informe um título para a mensagem"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (nvl(TxtMensagem.getText()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Informe valor a mensagem para enviar"));
            ModelDialog.getDialog().raise();
            return false;
        } else {
            return true;
        }
    }
}
