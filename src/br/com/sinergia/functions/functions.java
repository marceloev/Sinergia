package br.com.sinergia.functions;

import br.com.sinergia.models.statics.AppInfo;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class functions {

    public static SimpleDateFormat DataHoraFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static SimpleDateFormat DataFormater = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat HoraFormater = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat HourMinFormater = new SimpleDateFormat("HH:mm");
    public static Map<Integer, Image> MapCachImgUsers = new LinkedHashMap<>();

    public static String nvl(String valor) {
        if (valor == null) return "";
        else return valor;
    }

    public static String formatDate(SimpleDateFormat formater, String value) {
        if (nvl(value).equals("")) return "";
        else return formater.format(value);
    }

    public static String formatDate(SimpleDateFormat formater, Timestamp value) {
        if (value == null) return "";
        else return formater.format(value);
    }


    public static Boolean toBoo(String valor) {
        if (valor.equals("Sim") || valor.equals("S") || valor.equals("sim") || valor.equals("s")) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean toBoo(Integer valor) {
        if (valor.equals(1)) return true;
        else return false;
    }

    public static Integer getOnlyNumber(String valor) {
        return Integer.valueOf(valor.replaceAll("[^0-9]", ""));
    }

    public static StringBuilder arrayParameter(ArrayList Array) {
        StringBuilder parameterBuilder = new StringBuilder();
        parameterBuilder.append("(");
        for (int i = 0; i < Array.size(); i++) {
            parameterBuilder.append("?");
            if (Array.size() > i + 1) {
                parameterBuilder.append(",");
            }
        }
        parameterBuilder.append(")");
        return parameterBuilder;

    }

    public static void setLayoutPane(Pane paneToRezise) {
        Platform.runLater(() -> {
            paneToRezise.prefWidthProperty().bind(AppInfo.getMainTabPane().widthProperty().subtract(5));
            paneToRezise.prefHeightProperty().bind(AppInfo.getMainTabPane().heightProperty().subtract(30));
        });
    }
}
