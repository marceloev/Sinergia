package br.com.sinergia.models.statics;

import br.com.sinergia.controller.fxml.MensagemCtrl;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AppMensagens {

    private static Stage stage = new Stage();
    private static MensagemCtrl mensagemCtrl;
    private static Boolean estruturado = false;

    public static Stage getStage() {
        try {
            if (!estruturado) {
                FXMLLoader fxmlLoader = new FXMLLoader(AppMensagens.class.getResource("/br/com/sinergia/views/fxml/Mensagem.fxml"));
                AnchorPane root = fxmlLoader.load();
                mensagemCtrl = fxmlLoader.getController();
                Scene scene = new Scene(root);
                AppMensagens.stage.setScene(scene);
                AppMensagens.stage.setResizable(false);
                AppMensagens.stage.setTitle("Envio de novas mensagems");
                AppMensagens.stage.getIcons().add(new Image("/br/com/sinergia/views/images/Icone_Msg_Recebida.png"));
                AppMensagens.stage.initOwner(AppInfo.getStageMain());
                AppMensagens.stage.initModality(Modality.WINDOW_MODAL);
                estruturado = true;
            }
            mensagemCtrl.getTxtCodRemetente().setText(User.getCurrent().getCodUsu() + "");
            mensagemCtrl.getTxtDescrRemetente().setText(User.getCurrent().getLoginUsu());
            mensagemCtrl.getListDestinatarios().getItems().clear();
            mensagemCtrl.getTxtTitulo().clear();
            mensagemCtrl.getTxtMensagem().clear();
            return AppMensagens.stage;
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(AppMensagens.class, null,
                    "Erro ao tentar estruturar tela de mensagens\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
            return null;
        }
    }

    public static MensagemCtrl getMensagemCtrl() {
        return mensagemCtrl;
    }

    public static void setMensagemCtrl(MensagemCtrl mensagemCtrl) {
        AppMensagens.mensagemCtrl = mensagemCtrl;
    }


    public static void setStage(Stage stage) {
        AppMensagens.stage = stage;
    }
}
