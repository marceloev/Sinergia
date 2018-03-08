package br.com.sinergia.models.statics;

import javafx.scene.control.TabPane;

public class AppInfo {

    private static String versaoExec;
    private static String versaoDB;
    private static String arqTelasFav;
    private static TabPane mainTabPane;

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
}
