package br.com.sinergia.models.statics;

import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class AppInfo {

    private static String versaoExec;
    private static String versaoDB;
    private static TabPane mainTabPane;
    private static ArrayList<String> tabPanes = new ArrayList<>();
    private static ArrayList<String> strTelasFav = new ArrayList<>();
    private static VBox vBoxFavoritos;
    private static VBox vBoxRecentes;

    public static String getVersaoExec() {
        return versaoExec;
    }

    public static void setVersaoExec(String versaoExec) {
        AppInfo.versaoExec = versaoExec;
    }

    public static String getVersaoDB() {
        return versaoDB;
    }

    public static void setVersaoDB(String versaoDB) {
        AppInfo.versaoDB = versaoDB;
    }

    public static TabPane getMainTabPane() {
        return mainTabPane;
    }

    public static void setMainTabPane(TabPane mainTabPane) {
        AppInfo.mainTabPane = mainTabPane;
    }

    public static ArrayList<String> getTabPanes() {
        return tabPanes;
    }

    public static void setTabPanes(ArrayList<String> tabPanes) {
        AppInfo.tabPanes = tabPanes;
    }

    public static VBox getvBoxFavoritos() {
        return vBoxFavoritos;
    }

    public static void setvBoxFavoritos(VBox vBoxFavoritos) {
        AppInfo.vBoxFavoritos = vBoxFavoritos;
    }

    public static VBox getvBoxRecentes() {
        return vBoxRecentes;
    }

    public static void setvBoxRecentes(VBox vBoxRecentes) {
        AppInfo.vBoxRecentes = vBoxRecentes;
    }

    public static ArrayList<String> getStrTelasFav() {
        return strTelasFav;
    }

    public static void setStrTelasFav(ArrayList<String> strTelasFav) {
        AppInfo.strTelasFav = strTelasFav;
    }

    public void addPane() {};

}
