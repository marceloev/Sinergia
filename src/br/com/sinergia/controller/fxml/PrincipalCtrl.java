package br.com.sinergia.controller.fxml;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.functions.CtrlAccMenu;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.stage.Stage;

public class PrincipalCtrl extends Application {

    DBConn conex;

    @FXML
    private Accordion accMenus;

    @Override
    public void start(Stage primaryStage) {
        estrutura();
    }

    private void estrutura() {
        //Alimenta o maintab
        new CtrlAccMenu(accMenus);
    }
}
