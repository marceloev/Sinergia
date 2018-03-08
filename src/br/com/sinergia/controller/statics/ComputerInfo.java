package br.com.sinergia.controller.statics;

import javafx.beans.binding.DoubleBinding;

public class ComputerInfo {

    private static String IPMáquina;
    private static String NomeMáquina;
    private static String VersãoExec;
    private static String VersãoBD;
    private static DoubleBinding LayoutW;
    private static DoubleBinding LayoutH;

    public static String getIPMáquina() {
        return IPMáquina;
    }

    public static void setIPMáquina(String IPMáquina) {
        ComputerInfo.IPMáquina = IPMáquina;
    }

    public static String getNomeMáquina() {
        return NomeMáquina;
    }

    public static void setNomeMáquina(String nomeMáquina) {
        NomeMáquina = nomeMáquina;
    }

    public static String getVersãoExec() {
        return VersãoExec;
    }

    public static void setVersãoExec(String versãoExec) {
        VersãoExec = versãoExec;
    }

    public static String getVersãoBD() {
        return VersãoBD;
    }

    public static void setVersãoBD(String versãoBD) {
        VersãoBD = versãoBD;
    }

    public static DoubleBinding getLayoutW() {
        return LayoutW;
    }

    public static void setLayoutW(DoubleBinding layoutW) {
        LayoutW = layoutW;
    }

    public static DoubleBinding getLayoutH() {
        return LayoutH;
    }

    public static void setLayoutH(DoubleBinding layoutH) {
        LayoutH = layoutH;
    }
}
