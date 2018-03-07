package br.com.sinergia.controller.fxml;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.database.conector.DatabaseConf;
import br.com.sinergia.functions.log.GravaLog;
import br.com.sinergia.views.dialogs.ModelDialog;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class main extends Application {

    DBConn conex;

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/br/com/sinergia/views/fxml/example.fxml"));
            Scene scene = new Scene(root, 300, 275);
            primaryStage.setTitle("FXML Welcome");
            primaryStage.setScene(scene);
            primaryStage.show();
            try {
                DatabaseConf.setDatabaseConf("192.168.0.170", "1521/ORCL", "HDESNK03", "tecsis");
                conex = new DBConn(this.getClass(), false,
                        "SELECT CODUSU, NOMEUSU FROM TSIUSU WHERE CODUSU = 2");
                conex.createSet();
                conex.rs.next();
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION,
                        this.getClass(), null, conex.rs.getString(1) + " - " + conex.rs.getString(2)));
                ModelDialog.getDialog().raise();
                System.out.println("Foi");
            } catch (Exception ex) {
                System.out.println("Deu erro1");
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar conectar com banco de dados\n" + ex, ex));
                ModelException.getDialog().raise();
            }
        } catch (Exception ex) {
            System.out.println("Deu erro2");
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar conectar com banco de dados\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
        }
    }
}
