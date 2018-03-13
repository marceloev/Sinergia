package br.com.sinergia.controller.fxml;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.database.querys.ListaQuerys;
import br.com.sinergia.functions.MaskField;
import br.com.sinergia.models.usage.Mensagem;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelDialog;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static br.com.sinergia.functions.functions.*;

public class MensagemCtrl implements Initializable {

    @FXML
    private TextField TxtCodRemetente;
    @FXML
    private TextField TxtDescrRemetente;
    @FXML
    private TextField TxtTitulo;
    @FXML
    private TextArea TxtMensagem;
    @FXML
    public Button BtnEnviar;
    @FXML
    private ToggleButton TggDestinatarios;
    @FXML
    private Spinner<Integer> SpnPrioridade;
    @FXML
    public ImageView imgAdicionaDest, imgRemoveDest;
    @FXML
    private ListView<Pair<Integer, String>> ListDestinatarios;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
    }

    private void estrutura() {
        getTxtCodRemetente().setText(User.getCurrent().getCodUsu() + "");
        getTxtDescrRemetente().setText(User.getCurrent().getLoginUsu() + " - " + User.getCurrent().getNomeUsu());
        MaskField.SpnFieldCtrl(getSpnPrioridade(), 1, 3);
        getSpnPrioridade().getValueFactory().setValue(3);
        getTggDestinatarios().selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                imgAdicionaDest.setDisable(true);
                imgRemoveDest.setDisable(true);
                getListDestinatarios().setDisable(true);
            } else {
                imgAdicionaDest.setDisable(false);
                imgRemoveDest.setDisable(false);
                getListDestinatarios().setDisable(false);
            }
        });
        BtnEnviar.setOnAction(e -> {
            sendMessage(
                    new Mensagem(
                            getSpnPrioridade().getValueFactory().getValue(),
                            getTxtTitulo().getText(),
                            getTxtMensagem().getText()));
        });
    }

    public void ctrlDestinatario(Boolean adding, Pair<Integer, String> destinario) {
        long qtd = getListDestinatarios().getItems().stream().filter(destList -> destList.getKey().equals(destinario.getKey())).count();
        if (adding) {
            if (qtd > 0) {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                        "Usuário: " + destinario.getKey() + " - " + destinario.getValue() + "\n" +
                                "Já existe na lista de destinários."));
                ModelDialog.getDialog().raise();
            } else {
                getListDestinatarios().getItems().add(destinario);
            }
        } else {
            if (qtd == 0) {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                        "Usuário: " + destinario.getKey() + " - " + destinario.getValue() + "\n" +
                                "Não consta na lista de destinários."));
                ModelDialog.getDialog().raise();
            } else {
                getListDestinatarios().getItems().remove(getListDestinatarios().getItems().stream().filter(destList -> destList.getKey().equals(destinario.getKey())));
            }
        }

    }

    public static void sendMessage(int codUsu, Mensagem mensagem) {
        DBConn conex = null;
        try {
            conex = new DBConn(MensagemCtrl.class, false, ListaQuerys.getQueryInsertLembrete());
            conex.addParameter(codUsu);
            conex.addParameter("N");
            conex.addParameter(mensagem.getMensagem());
            conex.addParameter(mensagem.getPrioridade());
            conex.addParameter(mensagem.getDhAlter());
            conex.addParameter(mensagem.getTitulo());
            conex.addParameter(null);
            conex.addParameter(User.getCurrent().getCodUsu());
            conex.run();
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, MensagemCtrl.class, null,
                    "Mensagem enviada com sucesso!"));
            ModelDialog.getDialog().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(MensagemCtrl.class, null,
                    "Erro ao tentar enviar mensagem ao destinatário\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
        }
    }

    public static void sendMessage(ArrayList<Integer> arrayUsu, Mensagem mensagem) {
        DBConn conex = null;
        try {
            for (Integer codUsu : arrayUsu) {
                conex = new DBConn(MensagemCtrl.class, false, ListaQuerys.getQueryInsertLembrete());
                conex.addParameter(codUsu);
                conex.addParameter("N");
                conex.addParameter(mensagem.getMensagem());
                conex.addParameter(mensagem.getPrioridade());
                conex.addParameter(mensagem.getDhAlter());
                conex.addParameter(mensagem.getTitulo());
                conex.addParameter(null);
                conex.addParameter(User.getCurrent().getCodUsu());
                conex.run();
            }
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, MensagemCtrl.class, null,
                    "Mensagem enviada com sucesso!"));
            ModelDialog.getDialog().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(MensagemCtrl.class, null,
                    "Erro ao tentar enviar mensagens aos destinatários\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void sendMessage(Mensagem mensagem) {
        if (validMensagem()) {
            DBConn conex = null;
            try {
                List<String> listUsuDest = new ArrayList<>();
                if (getTggDestinatarios().isSelected()) {
                    conex = new DBConn(this.getClass(), false, ListaQuerys.getAgrListCodUsu());
                    conex.createSet();
                    conex.rs.next();
                    listUsuDest = Arrays.asList(conex.rs.getString(1).split(";"));
                } else {
                    List<String> finalListUsuDest = listUsuDest;
                    getListDestinatarios().getItems().forEach(destinatario -> {
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
        if (getListDestinatarios().getItems().isEmpty() && !getTggDestinatarios().isSelected()) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Informe ao menos um remetente para envio da mensagem"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (nvl(getTxtTitulo().getText()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Informe um título para a mensagem"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (nvl(getTxtMensagem().getText()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Informe valor a mensagem para enviar"));
            ModelDialog.getDialog().raise();
            return false;
        } else {
            return true;
        }
    }

    public TextField getTxtCodRemetente() {
        return TxtCodRemetente;
    }

    public TextField getTxtDescrRemetente() {
        return TxtDescrRemetente;
    }

    public TextField getTxtTitulo() {
        return TxtTitulo;
    }

    public TextArea getTxtMensagem() {
        return TxtMensagem;
    }

    public ToggleButton getTggDestinatarios() {
        return TggDestinatarios;
    }

    public Spinner<Integer> getSpnPrioridade() {
        return SpnPrioridade;
    }

    public ListView<Pair<Integer, String>> getListDestinatarios() {
        return ListDestinatarios;
    }
}
