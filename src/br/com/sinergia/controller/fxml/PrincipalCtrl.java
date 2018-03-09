package br.com.sinergia.controller.fxml;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.functions.CtrlAccMenu;
import br.com.sinergia.models.statics.AppInfo;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PrincipalCtrl extends Application {

    DBConn conex;

    @FXML
    private Accordion accMenus;
    @FXML
    private TabPane AbaPane;
    @FXML
    private VBox VBoxFavoritos, VBoxRecentes;

    @Override
    public void start(Stage primaryStage) {
        estrutura();
    }

    private void estrutura() {
        AppInfo.setMainTabPane(AbaPane);
        AppInfo.setvBoxFavoritos(VBoxFavoritos);
        AppInfo.setvBoxRecentes(VBoxRecentes);
        new CtrlAccMenu(accMenus);

    }
}
