package br.com.sinergia.models.extendeds;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.functions.functions;
import javafx.scene.input.Clipboard;
import br.com.sinergia.functions.log.GravaLog;
import br.com.sinergia.models.statics.AppInfo;
import br.com.sinergia.models.usage.Mensagem;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class PaneMessages extends ScrollPane {

    private static Boolean estrutured = false;
    private TitledPane ttp;
    private DBConn conex;
    private int codUltMsg = 0, qtdMsgNaoVisualizada = 0;
    private Timeline tmlGetNewMessages;
    private final Clipboard clipboard = Clipboard.getSystemClipboard();
    private final ClipboardContent content = new ClipboardContent();
    private ObservableList<Mensagem> messages = FXCollections.observableArrayList();
    private ListView<Mensagem> listMensagem = new ListView<>(messages);

    public PaneMessages(TitledPane ttpMensagens) {
        super();
        setTtp(ttpMensagens);
        estrutura();
        loadMensagens(true);
    }

    private void estrutura() {
        //Colocar contextmenu para COPIAR CONTEÚDO, RESPONDER, ENVIAR NOVA MENSAGEM;
        //Colocar botão de RESPONDER no showUnique, para gerar uma caixa de texto abaixo;
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
            listMensagem.setOnMouseClicked(e -> {
                if (e.getClickCount() > 1) {
                    if (listMensagem.getItems() != null
                            && listMensagem.getItems().get(listMensagem.getSelectionModel().getSelectedIndex()) != null) {
                        showUnique(listMensagem.getItems().get(listMensagem.getSelectionModel().getSelectedIndex()));
                    }
                }
            });
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItemNovaMsg = new MenuItem("Enviar nova mensagem");
            contextMenu.getItems().add(menuItemNovaMsg);
            MenuItem menuItemClipboard = new MenuItem("Copiar conteúdo");
            menuItemClipboard.setOnAction(e-> {
                if (listMensagem.getItems() != null
                        && listMensagem.getItems().get(listMensagem.getSelectionModel().getSelectedIndex()) != null) {
                    content.putString(listMensagem.getItems().get(listMensagem.getSelectionModel().getSelectedIndex()).getMensagem());
                    clipboard.setContent(content);
                }
            });
            contextMenu.getItems().add(menuItemClipboard);
            MenuItem menuItemResponder = new MenuItem("Responder");
            contextMenu.getItems().add(menuItemResponder);
            AppInfo.getBtnMensagens().setStyle("-fx-text-fill: RED");
            VBox root = new VBox();
            root.getChildren().add(listMensagem);
            root.setFillWidth(true);
            this.setContent(root);
            KeyFrame toDoGetNewMessages = new KeyFrame(Duration.millis(3000), e -> getNewMessages());
            tmlGetNewMessages = new Timeline(toDoGetNewMessages);
            tmlGetNewMessages.setCycleCount(Timeline.INDEFINITE);
            tmlGetNewMessages.play();
        }
    }

    private void loadMensagens(Boolean init) {
        if (init) listMensagem.getItems().clear();
        try {
            conex = new DBConn(this.getClass(), false, "SELECT * FROM \n" +
                    "(SELECT MSG.CODMSG, MSG.DHALTER, MSG.PRIORIDADE, MSG.TITULO, MSG.MENSAGEM, MSG.VISUALIZADA,\n" +
                    "CASE WHEN MSG.CODUSUREM IS NULL THEN -1 ELSE MSG.CODUSUREM END AS CODUSUREM,\n" +
                    "CASE WHEN MSG.CODUSUREM IS NULL THEN 'Desconhecido' ELSE USU.LOGIN END AS LOGINREM\n" +
                    "FROM TRIMSG MSG\n" +
                    "LEFT JOIN TSIUSU USU\n" +
                    "ON MSG.CODUSUREM = USU.CODUSU\n" +
                    "WHERE MSG.CODUSU = ?" +
                    "AND MSG.CODMSG > ?\n" +
                    "ORDER BY MSG.CODMSG DESC)\n" +
                    "WHERE ROWNUM <= 20");
            conex.addParameter(User.getCurrent().getCodUsu());
            conex.addParameter(codUltMsg);
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
            //Se apresentou mensagem, não vai ser mostrado erros.
            GravaLog.gravaErro(this.getClass(), "Erro ao tentar buscar mensagens\n" + ex.getMessage(), ex);
        } finally {
            conex.desconecta();
        }
    }

    private void getNewMessages() {
        try {
            conex = new DBConn(this.getClass(), false,
                    "SELECT COUNT(1) FROM TRIMSG WHERE CODMSG > ? AND CODUSU = ?");
            conex.addParameter(codUltMsg);
            conex.addParameter(User.getCurrent().getCodUsu());
            conex.rs.next();
            int count = conex.rs.getInt(1);
            if (count > 0) loadMensagens(false);
        } catch (Exception ex) {
            //Se apresentou mensagem, não vai ser mostrado erros.
            GravaLog.gravaErro(this.getClass(), "Erro ao tentar verificar se há novas mensagens\n" + ex.getMessage(), ex);
        } finally {
            conex.desconecta();
        }
    }

    private void setNewMessage(Mensagem newMessage) {
        tmlGetNewMessages.stop();
        messages.add(newMessage);
        if (newMessage.getCodMsg() > codUltMsg) {
            codUltMsg = newMessage.getCodMsg();
        }
        if (!newMessage.getVisualizada()) {
            getMsgNaoVisualizada(true);
            if (newMessage.getPrioridade() == 1) {
                Platform.runLater(() -> showUnique(newMessage)); //Tem que ser runlater se não causa NPE
            }
        }
        tmlGetNewMessages.play();
    }

    private void showUnique(Mensagem mensagem) {
        if (!mensagem.getVisualizada()) {
            mensagem.setVisualizada(true);
            getMsgNaoVisualizada(false);
        }
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.getDialogPane().getStylesheets().add("/br/com/sinergia/views/CssFiles/MsgDialog.css");
        alerta.setResizable(true);
        alerta.setTitle("Leitura de mensagens:");
        Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/br/com/sinergia/views/images/Icone_Msg_Visualizada.png"));
        ImageView img = new ImageView(mensagem.getImgUsu());
        img.setFitHeight(50);
        img.setFitWidth(50);
        Text txtPrioridade = new Text("Prioridade: ");
        switch (mensagem.getPrioridade()) {
            case 1:
                txtPrioridade.setText(txtPrioridade.getText() + "1");
                txtPrioridade.setFill(Color.RED);
                break;
            case 2:
                txtPrioridade.setText(txtPrioridade.getText() + "1");
                txtPrioridade.setFill(Color.YELLOW);
                break;
            case 3:
                txtPrioridade.setText(txtPrioridade.getText() + "1");
                txtPrioridade.setFill(Color.BLACK);
                break;
            default:
                ModelException.setNewException(new ModelException(this.getClass(), null,
                        "Erro ao tentar exibir mensagem\nValor não configurado para prioridade: " + mensagem.getPrioridade()));
                ModelException.getDialog().raise();
                return;
        }
        Text txtRem = new Text(mensagem.getCodUsu() + " - " + mensagem.getLoginUsu());
        txtRem.setFont(Font.font("System", 12));
        Text txtDH = new Text(functions.formatDate(functions.dataHoraFormater, mensagem.getDhAlter()));
        txtDH.setFont(Font.font("System", FontPosture.ITALIC, 11));
        VBox vBox = new VBox(txtPrioridade, txtRem, txtDH);
        HBox hbox1 = new HBox(vBox, new Text("  "), img);
        alerta.setGraphic(hbox1);
        alerta.setHeaderText(mensagem.getTitulo());
        alerta.setContentText(mensagem.getMensagem());
        alerta.show();
    }

    private void getMsgNaoVisualizada(Boolean adding) {
        if (adding) {
            qtdMsgNaoVisualizada = qtdMsgNaoVisualizada + 1;
            AppInfo.getStageMain().setTitle("[ " + qtdMsgNaoVisualizada + " ] Sistema Sinergia: ( " + User.getCurrent().getCodUsu() + " - " +
                    User.getCurrent().getLoginUsu() + " )");
            AppInfo.getBtnMensagens().setText("[ " + qtdMsgNaoVisualizada + " ]");
        } else {
            qtdMsgNaoVisualizada = qtdMsgNaoVisualizada - 1;
            if (qtdMsgNaoVisualizada == 0) {
                AppInfo.getStageMain().setTitle("Sistema Sinergia: ( " + User.getCurrent().getCodUsu() + " - " +
                        User.getCurrent().getLoginUsu() + " )");
                AppInfo.getBtnMensagens().setText("");
            } else {
                AppInfo.getStageMain().setTitle("[ " + qtdMsgNaoVisualizada + " ] Sistema Sinergia: ( " + User.getCurrent().getCodUsu() + " - " +
                        User.getCurrent().getLoginUsu() + " )");
                AppInfo.getBtnMensagens().setText("[ " + qtdMsgNaoVisualizada + " ]");
            }
        }
    }

    public TitledPane getTtp() {
        return ttp;
    }

    public void setTtp(TitledPane ttp) {
        this.ttp = ttp;
    }
}
