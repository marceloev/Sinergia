package br.com.sinergia.views.dialogs;

import javafx.geometry.NodeOrientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Optional;

public class ModelDialogButton {

    private static ModelDialogButton dialogButton;
    private Class Invocador;
    private String HeaderMsg;
    private String ContentMsg;
    private String TraceMsg;
    private Alert Alerta;
    private ButtonType[] BtnTypes;

    public ModelDialogButton(Class Invocador, String HeaderMsg, String ContentMsg, String TraceMsg) {
        setInvocador(Invocador);
        setHeaderMsg(HeaderMsg);
        setContentMsg(ContentMsg);
        setTraceMsg(TraceMsg);
        Alerta = new Alert(Alert.AlertType.NONE);
        Stage stage = (Stage) Alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/br/com/sinergia/views/images/Icone_Informação.png").toString()));
        stage.setTitle("Sistema Sinergia");
        Alerta.setHeaderText(getHeaderMsg());
        Alerta.setContentText(getContentMsg());
    }

    public ModelDialogButton(Class Invocador, Alert.AlertType AlertType, String HeaderMsg, String ContentMsg) {
        setInvocador(Invocador);
        setHeaderMsg(HeaderMsg);
        setContentMsg(ContentMsg);
        setTraceMsg(TraceMsg);
        Alerta = new Alert(AlertType);
        Alerta.getButtonTypes().clear();
        Stage stage = (Stage) Alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/br/com/sinergia/views/images/Icone_Informação.png").toString()));
        stage.setTitle("Sistema Sinergia");
        Alerta.setHeaderText(getHeaderMsg());
        Alerta.setContentText(getContentMsg());
    }

    public ModelDialogButton(Class Invocador, String HeaderMsg, String ContentMsg) {
        setInvocador(Invocador);
        setHeaderMsg(HeaderMsg);
        setContentMsg(ContentMsg);
        setTraceMsg(TraceMsg);
        Alerta = new Alert(Alert.AlertType.NONE);
        Stage stage = (Stage) Alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/br/com/sinergia/views/images/Icone_Informação.png").toString()));
        stage.setTitle("Sistema Sinergia");
        Alerta.setHeaderText(getHeaderMsg());
        Alerta.setContentText(getContentMsg());
    }

    public static int YesNoDialog(Class invoker, String título, String mensagem) {
        ModelDialogButton.setDialogButton(new ModelDialogButton(invoker, título, mensagem));
        ButtonType[] buttons = new ButtonType[2];
        buttons[0] = new ButtonType("Sim");
        buttons[1] = new ButtonType("Não");
        ModelDialogButton.getDialogButton().createButton(buttons);
        ButtonType getChoosed = ModelDialogButton.getDialogButton().returnChoosed();
        if(getChoosed == buttons[0]) return 1;
        else return 0;
    }

    public void createButton(ButtonType[] BtnTypes) {
        this.BtnTypes = BtnTypes;
        Alerta.getButtonTypes().addAll(BtnTypes);
    }

    public void addCheckBox(CheckBox ckb) {
        ckb.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        Node graphic = Alerta.getDialogPane().getGraphic();
        Alerta.setDialogPane(new DialogPane() {
            @Override
            protected Node createDetailsButton() {
                return ckb;
            }
        });
        Alerta.getDialogPane().setExpandableContent(new Group());
        Alerta.getDialogPane().setExpanded(true);
        Alerta.getDialogPane().setGraphic(graphic);
        Alerta.setContentText(getContentMsg());
        Alerta.setHeaderText(getHeaderMsg());
    }

    public ButtonType returnChoosed() {
        if(Alerta.getButtonTypes().size() == 0) {
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Classe ModelDialogButton invocado, mas sem botões, operação cancelada"));
            ModelException.getDialog().raise();
            return ButtonType.FINISH;
        }
        Optional<ButtonType> getChoosed = Alerta.showAndWait();
        return getChoosed.get();
    }

    public static ModelDialogButton getDialogButton() {
        return dialogButton;
    }

    public static void setDialogButton(ModelDialogButton dialogButton) {
        ModelDialogButton.dialogButton = dialogButton;
    }

    public Class getInvocador() {
        return Invocador;
    }

    public void setInvocador(Class invocador) {
        Invocador = invocador;
    }

    public String getHeaderMsg() {
        return HeaderMsg;
    }

    public void setHeaderMsg(String headerMsg) {
        HeaderMsg = headerMsg;
    }

    public String getContentMsg() {
        return ContentMsg;
    }

    public void setContentMsg(String contentMsg) {
        ContentMsg = contentMsg;
    }

    public String getTraceMsg() {
        return TraceMsg;
    }

    public void setTraceMsg(String traceMsg) {
        TraceMsg = traceMsg;
    }
}
