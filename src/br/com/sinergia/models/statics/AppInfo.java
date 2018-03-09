package br.com.sinergia.models.statics;

import javafx.scene.control.TabPane;

import java.util.ArrayList;

public class AppInfo {

    private static String versaoExec;
    private static String versaoDB;
    private static String arqTelasFav;
    private static TabPane mainTabPane;
    private static ArrayList<String> tabPanes = new ArrayList<>();

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

    public static String getArqTelasFav() {
        return arqTelasFav;
    }

    public static void setArqTelasFav(String arqTelasFav) {
        AppInfo.arqTelasFav = arqTelasFav;
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

    public void addPane() {};

}
