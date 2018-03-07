package br.com.sinergia.database.conector;

import br.com.sinergia.functions.log.GravaLog;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DBConn {

    public ResultSet rs;
    private PreparedStatement pst;
    private Connection con;
    private String query;
    private static ArrayList<DBConnModel> connections = new ArrayList<>();
    private DBConnModel connection;
    private Integer index = 1;

    public DBConn(Class invoker, Boolean hide, String query) throws SQLException {
        super();
        index = 1;
        connection = new DBConnModel(invoker, hide, false, 0);
        conecta();
        setQuery(query);
        if (!connection.getHide()) {
            GravaLog.gravaInfo(connection.getInvoker(), getQuery());
        }
        getConnections().add(connection);
        pst = con.prepareStatement(getQuery(), ResultSet.TYPE_SCROLL_INSENSITIVE);
    }

    public DBConn(Class invoker, Boolean hide, int secTimeOut, String query) throws SQLException {
        super();
        index = 1;
        connection = new DBConnModel(invoker, hide, true, secTimeOut);
        conecta();
        setQuery(query);
        if (!connection.getHide()) {
            GravaLog.gravaInfo(connection.getInvoker(), getQuery());
        }
        getConnections().add(connection);
        pst = con.prepareStatement(getQuery(), ResultSet.TYPE_SCROLL_INSENSITIVE);
        startTimer();
    }

    private void conecta() throws SQLException {
        if (!DatabaseConf.isConfigurado()) {
            throw new SQLException("Conexão com banco de dados não configurada!\n" +
                    "Operação cancelada.");
        }
        System.setProperty("jdbc.Drivers", DatabaseConf.driver);
        System.out.println(DatabaseConf.getCaminho());
        Properties jdbcProperties = new Properties();
        jdbcProperties.put("user", DatabaseConf.getUsuario());
        jdbcProperties.put("password", DatabaseConf.getSenha());
        jdbcProperties.put("v$session.program", "Sinergia");
        jdbcProperties.put("ServiceName", "Cadastro de Produtos");
        con = DriverManager.getConnection(DatabaseConf.getCaminho(), jdbcProperties);
        con.setClientInfo("oi", "oi");
    }

    public void createSet() throws SQLException {
        long time = System.currentTimeMillis();
        this.rs = pst.executeQuery();
        if (!connection.getExpires()) {
            time = System.currentTimeMillis() - time;
            GravaLog.gravaInfo(this.getClass(), "DB SourceConnection: ResulSet criado em " + time + " milisegundo(s)");
        }
    }

    public void desconecta() {
        try {
            con.close();
            getConnections().remove(connection);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar desconectar com banco de dados\n" + ex, ex));
            ModelException.getDialog().raise();
        }
    }

    public void startTimer() {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(connection.getTimeOutExpire() * 1000), //segundos -> millisegundos
                ae -> desconecta()));
        timeline.play();
    }

    public static ArrayList<DBConnModel> getConnections() {
        return connections;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
