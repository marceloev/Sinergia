package br.com.sinergia.database.conector;

import br.com.sinergia.functions.log.GravaLog;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.sql.*;
import java.time.LocalDate;
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

    public DBConn(Class invoker, String query) throws SQLException {
        super();
        index = 1;
        connection = new DBConnModel(invoker, false, false, 0);
        conecta();
        setQuery(query);
        if (!connection.getHide()) {
            GravaLog.gravaInfo(connection.getInvoker(), getQuery());
        }
        getConnections().add(connection);
        pst = con.prepareStatement(getQuery());
    }

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
        pst = con.prepareStatement(getQuery());
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
        pst = con.prepareStatement(getQuery());
        startTimer();
    }

    private void conecta() throws SQLException {
        if (!DatabaseConf.isConfigurado()) {
            throw new SQLException("Conexão com banco de dados não configurada!\n" +
                    "Operação cancelada.");
        }
        System.setProperty("jdbc.Drivers", DatabaseConf.driver);
        Properties jdbcProperties = new Properties();
        jdbcProperties.put("user", DatabaseConf.getUsuario());
        jdbcProperties.put("password", DatabaseConf.getSenha());
        jdbcProperties.put("v$session.program", "Sinergia");
        jdbcProperties.put("ServiceName", "Cadastro de Produtos");
        con = DriverManager.getConnection(DatabaseConf.getCaminho(), jdbcProperties);
    }

    public void createSet() throws SQLException {
        long time = System.currentTimeMillis();
        this.rs = pst.executeQuery();
        if (!connection.getHide()) {
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

    public void run() throws SQLException {
        long time = System.currentTimeMillis();
        if (!connection.getHide()) {
            int rows = pst.executeUpdate();
            time = System.currentTimeMillis() - time;
            GravaLog.gravaInfo(this.getClass(), "DB SourceConnection: Statement executado em " + time + " milisegundo(s)");
            GravaLog.gravaInfo(this.getClass(), rows + " linha(s) afetadas.");
        }
    }

    public void addParameter(Object objeto) throws SQLException {
        if (objeto == null) {
            addNullParameter();
            return;
        }
        String classType = objeto.getClass().getTypeName();
        if (!connection.getHide()) {
            GravaLog.gravaInfo(connection.getInvoker(), index + "º Parâmetro(" + classType + "): " + objeto);
        }
        addToStatement(index, objeto, classType);
        index++;
    }

    public void addParameter(Object objeto, String info) throws SQLException {
        if (objeto == null) {
            addNullParameter();
            return;
        }
        String classType = objeto.getClass().getTypeName();
        if (!connection.getHide()) {
            GravaLog.gravaInfo(connection.getInvoker(), index + "º Parâmetro(" + classType + "): " + info);
        }
        addToStatement(index, objeto, classType);
        index++;
    }

    private void addNullParameter() throws SQLException {
        if (!connection.getHide()) {
            GravaLog.gravaInfo(connection.getInvoker(), index + "º Parâmetro(java.lang.NullParam): Null");
        }
        addToStatement(index, null, "java.lang.NullParam");
        index++;
    }

    private void addToStatement(int index, Object objeto, String type) throws SQLException {
        if (objeto == null) type = "java.lang.NullParam";
        switch (type) {
            case "java.lang.NullParam":
                pst.setObject(index, null);
                break;
            case "java.lang.Double":
                pst.setDouble(index, (Double) objeto);
                break;
            case "java.lang.Integer":
                pst.setInt(index, (Integer) objeto);
                break;
            case "java.sql.Date":
                pst.setDate(index, (Date) objeto);
                break;
            case "java.time.LocalDate":
                pst.setDate(index, Date.valueOf((LocalDate) objeto));
                break;
            case "java.lang.Character":
                pst.setString(index, objeto.toString());
                break;
            case "java.lang.String":
                pst.setString(index, (String) objeto);
                break;
            case "java.sql.Timestamp":
                pst.setTimestamp(index, (Timestamp) objeto);
                break;
            case "byte[]":
                pst.setBytes(index, (byte[]) objeto);
                break;
            case "java.util.ArrayList":
                ArrayList<Object> arrayObj = (ArrayList<Object>) objeto;
                for (Object obj : arrayObj) {
                    String objType = obj.getClass().getTypeName();
                    GravaLog.gravaInfo(connection.getInvoker(), index + "º Parâmetro(" + objType + "): " + obj);
                    addToStatement(index, obj, objType);
                    index = index + 1;
                }
                this.index = index - 1;
                break;
            default:
                throw new SQLException("Erro ao tentar inserir parâmetro no Statement\n"
                        + "Tipo: " + type + ", não configurado para esta operação");
        }
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
