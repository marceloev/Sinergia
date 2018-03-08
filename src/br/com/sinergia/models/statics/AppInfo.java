package br.com.sinergia.models.statics;

public class AppInfo {

    private static String versaoExec;
    private static String versaoDB;
    private static String arqTelasFav;

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
}
