package br.com.sinergia.models.extendeds;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.functions.functions;
import br.com.sinergia.models.statics.AppInfo;
import br.com.sinergia.models.usage.Mensagem;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class PaneMessages extends ScrollPane {

    private static Boolean estrutured = false;
    private TitledPane ttp;
    private DBConn conex;
    private int codUltMsg = 0, qtdMsgNaoVisualizada = 0;
    private Timeline tmlGetNewMessages;
    private ObservableList<Mensagem> messages = FXCollections.observableArrayList();
    private ListView<Mensagem> listMensagem = new ListView<>(messages);

    public PaneMessages(TitledPane ttpMensagens) {
        super();
        setTtp(ttpMensagens);
        estrutura();
        loadMensagens();
    }

    private void estrutura() {
        //Colocar contextmenu para RESPONDER, ENVIAR NOVA MENSAGEM;
        if (!estrutured) {
            this.setFitToHeight(true);
            this.setFitToWidth(true);
            listMensagem.setCellFactory(new Callback<ListView<Mensagem>, ListCell<Mensagem>>() {
                @Override
                public ListCell<Mensagem> call(ListView<Mensagem> arg0) {
                    return new ListCell<Mensagem>() {
                        @Override
                        protected void updateItem(Mensagem msg, boolean bln) {
                            super.updateItem(msg, bln);
                            if (msg != null) {
                                ImageView imgRem = new ImageView(msg.getImgUsu());
                                imgRem.setFitHeight(50);
                                imgRem.setFitWidth(42);
                                Text txtTitulo = new Text(msg.getTitulo());
                                txtTitulo.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
                                txtTitulo.setFill(Color.BLACK);
                                ImageView imgStatusMsg;
                                if (msg.getVisualizada()) {
                                    imgStatusMsg = new ImageView("/br/com/sinergia/views/images/Icone_Msg_Visualizada.png");
                                    setStyle("-fx-control-inner-background: LightGray");
                                } else {
                                    imgStatusMsg = new ImageView("/br/com/sinergia/views/images/Icone_Msg_Pendente.png");
                                    setStyle("-fx-control-inner-background: White");
                                }
                                imgStatusMsg.setFitWidth(16);
                                imgStatusMsg.setFitHeight(16);
                                Text txtLogin = new Text(msg.getLoginUsu());
                                txtLogin.setFont(Font.font("System", 12));
                                Text txtDHMsg = new Text(functions.formatDate(functions.dataHoraFormater, msg.getDhAlter()));
                                txtDHMsg.setFont(Font.font("System", FontPosture.ITALIC, 12));
                                txtDHMsg.setFill(Color.valueOf("#807c7c"));
                                HBox Box = new HBox(txtTitulo, new Text(" "), imgStatusMsg);
                                HBox Box1 = new HBox(txtLogin);
                                HBox Box2 = new HBox(txtDHMsg);
                                VBox Box3 = new VBox(Box, Box1, Box2);
                                HBox hBox = new HBox(imgRem, Box3);
                                hBox.setSpacing(4);
                                setGraphic(hBox);
                            }
                        }
                    };
                }
            });
            VBox root = new VBox();
            root.getChildren().add(listMensagem);
            root.setFillWidth(true);
            this.setContent(root);
        }
    }

    private void loadMensagens() {
        listMensagem.getItems().clear();
        try {
            conex = new DBConn(this.getClass(), false, "SELECT * FROM \n" +
                    "(SELECT MSG.CODMSG, MSG.DHALTER, MSG.PRIORIDADE, MSG.TITULO, MSG.MENSAGEM, MSG.VISUALIZADA,\n" +
                    "CASE WHEN MSG.CODUSUREM IS NULL THEN -1 ELSE MSG.CODUSUREM END AS CODUSUREM,\n" +
                    "CASE WHEN MSG.CODUSUREM IS NULL THEN 'Desconhecido' ELSE USU.LOGIN END AS LOGINREM\n" +
                    "FROM TRIMSG MSG\n" +
                    "LEFT JOIN TSIUSU USU\n" +
                    "ON MSG.CODUSUREM = USU.CODUSU\n" +
                    "WHERE MSG.CODUSU = ?\n" +
                    "ORDER BY MSG.CODMSG DESC)\n" +
                    "WHERE ROWNUM <= 20");
            conex.addParameter(User.getCurrent().getCodUsu());
            conex.createSet();
            while (conex.rs.next()) {
                setNewMessage(new Mensagem(
                        conex.rs.getInt(1),
                        conex.rs.getInt("CODUSUREM"),
                        conex.rs.getString("LOGINREM"),
                        conex.rs.getTimestamp(2),
                        conex.rs.getInt(3),
                        conex.rs.getString(4),
                        conex.rs.getString(5),
                        functions.toBoo(conex.rs.getString(6)),
                        functions.getImageUsu(conex.rs.getInt("CODUSUREM"))
                ));
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar atualizar mensagens do usuário\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
            listMensagem.getItems().clear();
        } finally {
            conex.desconecta();
        }
    }

    private void setNewMessage(Mensagem newMessage) {
        //loopGetNewMessages.stop();
        messages.add(newMessage);
        if (newMessage.getCodMsg() > codUltMsg) {
            codUltMsg = newMessage.getCodMsg();
        }
        if (!newMessage.getVisualizada()) {
            getMsgNaoVisualizada(true);
        }
        //if (!newMessage.getVisualizada()) {
        //   attQtdMsgVisualizadas(true);
        //    if (newMessage.getPrioridadeMsg() == 1) {
        //        Platform.runLater(() -> showUnique(newMessage)); //Tem que ser runlater se não causa NPE
        //    }
        // }
        //loopGetNewMessages.play();
    }

    private void getMsgNaoVisualizada(Boolean adding) {
        if (adding) {
            qtdMsgNaoVisualizada = qtdMsgNaoVisualizada + 1;
            AppInfo.getStageMain().setTitle("[ " + qtdMsgNaoVisualizada + " ] Sistema Sinergia: ( " + User.getCurrent().getCodUsu() + " - " +
                    User.getCurrent().getLoginUsu() + " )");
        } else {
            qtdMsgNaoVisualizada = qtdMsgNaoVisualizada - 1;
            if (qtdMsgNaoVisualizada == 0)
                AppInfo.getStageMain().setTitle("Sistema Sinergia: ( " + User.getCurrent().getCodUsu() + " - " +
                        User.getCurrent().getLoginUsu() + " )");
            else
                AppInfo.getStageMain().setTitle("[ " + qtdMsgNaoVisualizada + " ] Sistema Sinergia: ( " + User.getCurrent().getCodUsu() + " - " +
                        User.getCurrent().getLoginUsu() + " )");
        }
    }

    public TitledPane getTtp() {
        return ttp;
    }

    public void setTtp(TitledPane ttp) {
        this.ttp = ttp;
    }
}
