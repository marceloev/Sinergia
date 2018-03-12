package br.com.sinergia.functions;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.models.statics.AppInfo;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class functions {

    public static SimpleDateFormat dataHoraFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static SimpleDateFormat dataFormater = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat horaFormater = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat horaMinFormater = new SimpleDateFormat("HH:mm");
    public static Map<Integer, Image> mapCachImgUsers = new LinkedHashMap<>();

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

    public static Image getImageUsu(int codUsu) {
        if (mapCachImgUsers.containsKey(codUsu)) return mapCachImgUsers.get(codUsu);
        else if (codUsu == -1) return null;
        else {
            DBConn conex = null;
            try {
                conex = new DBConn(functions.class, false,
                        "SELECT FOTO FROM TSIUSU WHERE CODUSU = ?");
                conex.addParameter(codUsu);
                conex.createSet();
                if (conex.rs.next()) {
                    if (conex.rs.getBytes(1) == null) {
                        mapCachImgUsers.put(codUsu, null);
                        return null;
                    } else {
                        InputStream input = new ByteArrayInputStream(conex.rs.getBytes(1));
                        Image imgUsu = new Image(input);
                        mapCachImgUsers.put(codUsu, imgUsu);
                        return imgUsu;
                    }
                } else {
                    ModelException.setNewException(new ModelException(functions.class, null,
                            "Não encontrado usuário para código: " + codUsu));
                    ModelException.getDialog().raise();
                    mapCachImgUsers.put(codUsu, null);
                    return null;
                }
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(functions.class, null,
                        "Erro ao tentar obter foto do usuário: " + codUsu + "\n" + ex.getMessage(), ex));
                ModelException.getDialog().raise();
                mapCachImgUsers.put(codUsu, null);
                return null;
            } finally {
                conex.desconecta();
            }
        }
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
