package br.com.sinergia.controller.fxml;

import br.com.sinergia.functions.frames.Tela;
import br.com.sinergia.functions.frames.Telas;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Telas.loadConf();
            Tela tela = Telas.getByCod(0);
            Parent root = FXMLLoader.load(getClass().getResource(tela.getFounder()));
            Scene scene = new Scene(root);
            primaryStage.getIcons().add(new Image("/br/com/sinergia/views/images/Icone_Sistema.png"));
            primaryStage.setTitle(tela.getDescrTela());
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX((screenBounds.getWidth() - primaryStage.getWidth()) / 2);
            primaryStage.setY((screenBounds.getHeight() - primaryStage.getHeight()) / 2);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar conectar com banco de dados\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        }
    }
}
