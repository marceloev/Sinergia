package br.com.sinergia.models.statics;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelException;

import java.sql.Timestamp;

public class Lembrete {

    private static Lembrete userLembrete;

    private int codUsu;
    private Timestamp dhLembrete;
    private String lembrete;

    public Lembrete(int codUsu, Timestamp dhLembrete, String lembrete) {
        super();
        setCodUsu(codUsu);
        setDhLembrete(dhLembrete);
        setLembrete(lembrete);
    }

    public static Lembrete getUserLembrete() {
        if (userLembrete == null || userLembrete.getCodUsu() != User.getCurrent().getCodUsu()) {
            DBConn conex = null;
            try {
                conex = new DBConn(Lembrete.class, false,
                        "SELECT LEMBRETE, DHALTER \n" +
                                "FROM TGFLEM LEM\n" +
                                "WHERE CODUSU = ?\n" +
                                "AND DHALTER = (SELECT MAX(DHALTER) FROM TGFLEM WHERE CODUSU = ?)");
                conex.addParameter(User.getCurrent().getCodUsu());
                conex.addParameter(User.getCurrent().getCodUsu());
                conex.createSet();
                if (conex.rs.next()) {
                    setUserLembrete(new Lembrete(User.getCurrent().getCodUsu(),
                            conex.rs.getTimestamp(2),
                            conex.rs.getString(1)));
                    return userLembrete;
                } else {
                    return null;
                }
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(Lembrete.class, null,
                        "Erro ao tentar buscar lembretes do usuário\n" + ex.getMessage(), ex));
                ModelException.getDialog().raise();
                return null;
            } finally {
                conex.desconecta();
            }
        } else return userLembrete;
    }

    public static void setUserLembrete(Lembrete userLembrete) {
        DBConn conex = null;
        try {
            conex = new DBConn(Lembrete.class, false,
                    "INSERT INTO TGFLEM (CODUSU, DHALTER, LEMBRETE)\n" +
                            "VALUES (?, ?, ?)");
            conex.addParameter(userLembrete.getCodUsu());
            conex.addParameter(userLembrete.getDhLembrete());
            conex.addParameter(userLembrete.getLembrete());
            conex.run();
            Lembrete.userLembrete = userLembrete;
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(Lembrete.class, null,
                    "Erro ao tentar registrar novo lembrete\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
        }
    }

    public int getCodUsu() {
        return codUsu;
    }

    public void setCodUsu(int codUsu) {
        this.codUsu = codUsu;
    }

    public Timestamp getDhLembrete() {
        return dhLembrete;
    }

    public void setDhLembrete(Timestamp dhLembrete) {
        this.dhLembrete = dhLembrete;
    }

    public String getLembrete() {
        return lembrete;
    }

    public void setLembrete(String lembrete) {
        this.lembrete = lembrete;
    }
}
