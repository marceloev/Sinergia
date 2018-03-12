package br.com.sinergia.functions;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelException;

import java.util.ArrayList;

public class CtrlArquivos {

    private static DBConn conex;

    public static void registra(int codUsu, String arquivo, String lista) {
        int count = 0;
        try {
            conex = new DBConn(CtrlArquivos.class, false, "SELECT COUNT(1) AS COUNT\n" +
                    "FROM TSIARQ ARQ\n" +
                    "WHERE ARQ.CODUSU = ?\n" +
                    "AND ARQ.ARQUIVO = ?");
            conex.addParameter(codUsu);
            conex.addParameter(arquivo);
            conex.createSet();
            conex.rs.next();
            count = conex.rs.getInt(1);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(CtrlArquivos.class, null, "Erro ao verificar se arquivo de configuração existia:\n" +
                    "Arquivo: " + arquivo + "\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
        }
        try {
            if (count == 1) {
                conex = new DBConn(CtrlArquivos.class, false, "UPDATE TSIARQ SET LISTA = ? \n"
                        + "WHERE CODUSU = ? AND ARQUIVO = ?");
                conex.addParameter(lista);
                conex.addParameter(codUsu);
                conex.addParameter(arquivo);
                conex.run();
            } else {
                conex = new DBConn(CtrlArquivos.class, false, "INSERT INTO TSIARQ\n" +
                        "(CODUSU, ARQUIVO, LISTA)\n" +
                        "VALUES\n" +
                        "(?, ?, ?)");
                conex.addParameter(codUsu);
                conex.addParameter(arquivo);
                conex.addParameter(lista);
                conex.run();
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(CtrlArquivos.class, null, "Erro ao tentar registrar arquivo de configuração:\n" +
                    "Arquivo: " + arquivo + "\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
        }
    }

    public static void registra(int codUsu, String arquivo, ArrayList<String> lista) {
        int count = 0;
        try {
            conex = new DBConn(CtrlArquivos.class, false, "SELECT COUNT(1) AS COUNT\n" +
                    "FROM TSIARQ ARQ\n" +
                    "WHERE ARQ.CODUSU = ?\n" +
                    "AND ARQ.ARQUIVO = ?");
            conex.addParameter(codUsu);
            conex.addParameter(arquivo);
            conex.createSet();
            conex.rs.next();
            count = conex.rs.getInt(1);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(CtrlArquivos.class, null, "Erro ao verificar se arquivo de configuração existia:\n" +
                    "Arquivo: " + arquivo + "\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
        }
        try {
            if (count == 1) {
                conex = new DBConn(CtrlArquivos.class, false, "UPDATE TSIARQ SET LISTA = ? \n"
                        + "WHERE CODUSU = ? AND ARQUIVO = ?");
                conex.addParameter(arrayLista(lista).toString());
                conex.addParameter(codUsu);
                conex.addParameter(arquivo);
                conex.run();
            } else {
                conex = new DBConn(CtrlArquivos.class, false, "INSERT INTO TSIARQ\n" +
                        "(CODUSU, ARQUIVO, LISTA)\n" +
                        "VALUES\n" +
                        "(?, ?, ?)");
                conex.addParameter(codUsu);
                conex.addParameter(arquivo);
                conex.addParameter(arrayLista(lista).toString());
                conex.run();
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(CtrlArquivos.class, null, "Erro ao tentar registrar arquivo de configuração:\n" +
                    "Arquivo: " + arquivo + "\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
        }
    }

    public static String busca(int codUsu, String arquivo) {
        try {
            conex = new DBConn(CtrlArquivos.class, false,
                    "SELECT LISTA FROM TSIARQ ARQ\n" +
                            "WHERE ARQ.CODUSU = ?\n" +
                            "AND ARQ.ARQUIVO = ?");
            conex.addParameter(codUsu);
            conex.addParameter(arquivo);
            conex.createSet();
            if (conex.rs.next()) {
                return conex.rs.getString(1);
            } else {
                return null;
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(CtrlArquivos.class, null,
                    "Erro ao tentar buscar arquivo de configuração:\n" +
                            "Arquivo: " + arquivo + "\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
        }
        return null;
    }

    public static StringBuilder arrayLista(ArrayList array) {
        StringBuilder strBuild = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if(i != array.size() - 1) strBuild.append(array.get(i) + ";");
            else strBuild.append(array.get(i));
        }
        return strBuild;

    }
}