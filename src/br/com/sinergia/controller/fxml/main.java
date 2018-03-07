package br.com.sinergia.controller.fxml;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.functions.frames.Telas;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.time.Instant;

import static br.com.sinergia.functions.functions.*;

public class main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Telas.loadConf();
            Telas.Tela tela = Telas.getByCod(0);
            Parent root = FXMLLoader.load(getClass().getResource(tela.getFounder()));
            Scene scene = new Scene(root, 300, 275);
            primaryStage.setTitle(tela.getDescrTela());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception ex) {
            System.out.println("Deu erro2");
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar conectar com banco de dados\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        }
    }
}
