package br.com.sinergia.database.conector;

public class DatabaseConf{

    public final static String driver = "oracle.jdbc.OracleDriver";
    private static String ip = "127.0.0.1";
    private static String porta = "1521/XE";
    private static String usuario = "sinergia";
    private static String senha = "tecsis";
    private static Boolean configurado = false;

    public static String getIP() {
        return ip;
    }

    public static void setIP(String ip) {
        DatabaseConf.ip = ip;
    }

    public static String getPorta() {
        return porta;
    }

    public static void setPorta(String porta) {
        DatabaseConf.porta = porta;
    }

    public static String getUsuario() {
        return usuario;
    }

    public static void setUsuario(String usuario) {
        DatabaseConf.usuario = usuario;
    }

    public static String getSenha() {
        return senha;
    }

    public static void setSenha(String senha) {
        DatabaseConf.senha = senha;
    }

    public static Boolean isConfigurado() {
        return configurado;
    }

    public static void setConfigurado(Boolean configurado) {
        DatabaseConf.configurado = configurado;
    }

    public static void setDatabaseConf(String ip, String porta, String usuario, String senha) {
        setIP(ip);
        setPorta(porta);
        setUsuario(usuario);
        setSenha(senha);
        setConfigurado(true);
    }

    public static String getCaminho() {
        return "jdbc:oracle:thin:@" + getIP() + ":" + getPorta();
    }
}