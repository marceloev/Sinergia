package br.com.sinergia.functions;

import javafx.scene.image.Image;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
}
