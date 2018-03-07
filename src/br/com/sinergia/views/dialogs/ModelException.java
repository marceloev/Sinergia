package br.com.sinergia.views.dialogs;

import br.com.sinergia.functions.log.GravaLog;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

public final class ModelException extends Dialog {

    private static ModelException modelException;
    private Class invoker;
    private String titulo;
    private String mensagem;
    private Throwable execption;

    public ModelException(Class invoker, String titulo, String mensagem) {
        super();
        setInvoker(invoker);
        if (titulo == null) setTitulo("Sistema Sinergia:");
        else setTitulo(titulo);
        setMensagem(mensagem);
        setExecption(null);
    }

    public ModelException(Class invoker, String titulo, String mensagem, Throwable execption) {
        super();
        setInvoker(invoker);
        if (titulo == null) setTitulo("Sistema Sinergia:");
        else setTitulo(titulo);
        setMensagem(mensagem);
        setExecption(execption);
    }


    public static ModelException getDialog() {
        return modelException;
    }

    public static void setNewException(ModelException newModelException) {
        modelException = newModelException;
    }

    public void raise() {
        GridPane expContent = new GridPane();
        if (getExecption() != null) {
            GravaLog.gravaErro(getInvoker(), getMensagem(), getExecption());
            /*Criamos uma expensão pois, foi uma exception e tem stacktrace
            Quando não é exception, ou seja, erro criado, não tem porque criar*/
            CharArrayWriter cw = new CharArrayWriter();
            PrintWriter w = new PrintWriter(cw);
            execption.printStackTrace(w);
            w.close();
            String exceptionText = cw.toString();
            Label label = new Label("Veja o caminho completo do erro:");
            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            //textArea.setWrapText(true); Questão de dentição da mensagem de erro
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);
        } else {
            GravaLog.gravaErro(getInvoker(), getMensagem());
        }
        Alert Alerta = new Alert(Alert.AlertType.ERROR);
        Alerta.setTitle("Sistema Sinergia:");
        Stage stage = (Stage) Alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/br/com/sinergia/views/images/Icone_Error.png").toString()));
        Alerta.setHeaderText(getTitulo());
        Alerta.setContentText(getMensagem());
        Alerta.setGraphic(null);
        if (getExecption() != null) {
            Alerta.getDialogPane().setExpandableContent(expContent);
        }
        Alerta.showAndWait();
        stage.setY(stage.getY() * 3f / 2f); //On center
    }

    public Class getInvoker() {
        return invoker;
    }

    public void setInvoker(Class invoker) {
        this.invoker = invoker;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Throwable getExecption() {
        return execption;
    }

    public void setExecption(Throwable execption) {
        this.execption = execption;
    }
}
