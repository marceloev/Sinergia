package br.com.sinergia.functions;

public class functions {

    public static Boolean toBoo(String valor) {
        if(valor.equals("Sim") || valor.equals("S") || valor.equals("sim") || valor.equals("s")) {
            return true;
        } else {
            return false;
        }
    }
}
