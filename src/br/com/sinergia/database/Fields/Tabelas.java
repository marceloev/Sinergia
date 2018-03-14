package br.com.sinergia.database.Fields;

import java.util.ArrayList;

public class Tabelas {

    private int idTabela;
    private String nomeTabela;
    private String pesqTabela;
    private String descrComplTabela;
    private ArrayList<Campos> tabCampos = new ArrayList<>();

    public Tabelas(int idTabela, String nomeTabela, String pesqTabela, String descrComplTabela) {
        super();
        this.idTabela = idTabela;
        this.nomeTabela = nomeTabela;
        this.pesqTabela = pesqTabela;
        this.descrComplTabela = descrComplTabela;
    }
    public Tabelas(int idTabela, String nomeTabela, String pesqTabela, String descrComplTabela, ArrayList<Campos> tabCampos) {
        super();
        this.idTabela = idTabela;
        this.nomeTabela = nomeTabela;
        this.pesqTabela = pesqTabela;
        this.descrComplTabela = descrComplTabela;
        this.tabCampos = tabCampos;
    }

    public int getIdTabela() {
        return idTabela;
    }

    public void setIdTabela(int idTabela) {
        this.idTabela = idTabela;
    }

    public String getNomeTabela() {
        return nomeTabela;
    }

    public void setNomeTabela(String nomeTabela) {
        this.nomeTabela = nomeTabela;
    }

    public String getPesqTabela() {
        return pesqTabela;
    }

    public void setPesqTabela(String pesqTabela) {
        this.pesqTabela = pesqTabela;
    }

    public String getDescrComplTabela() {
        return descrComplTabela;
    }

    public void setDescrComplTabela(String descrComplTabela) {
        this.descrComplTabela = descrComplTabela;
    }

    public ArrayList<Campos> getTabCampos() {
        return tabCampos;
    }

    @Override
    public String toString() {
        return "Tabelas{" +
                "idTabela=" + idTabela +
                ", nomeTabela='" + nomeTabela + '\'' +
                ", pesqTabela='" + pesqTabela + '\'' +
                ", descrComplTabela='" + descrComplTabela + '\'' +
                ", tabCampos=" + tabCampos +
                '}';
    }

    public void setTabCampos(ArrayList<Campos> tabCampos) {
        this.tabCampos = tabCampos;
    }
}
