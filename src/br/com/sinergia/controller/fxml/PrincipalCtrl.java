package br.com.sinergia.controller.fxml;

import br.com.sinergia.functions.CtrlAccMenu;
import br.com.sinergia.models.statics.AppInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PrincipalCtrl implements Initializable {

    @FXML
    private Accordion accMenus;
    @FXML
    private TabPane AbaPane;
    @FXML
    private VBox VBoxFavoritos, VBoxRecentes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
    }

    private void estrutura() {
        AppInfo.setMainTabPane(AbaPane);
        AppInfo.setvBoxFavoritos(VBoxFavoritos);
        AppInfo.setvBoxRecentes(VBoxRecentes);
        new CtrlAccMenu(accMenus);
    }
}
