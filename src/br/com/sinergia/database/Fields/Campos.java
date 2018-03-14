package br.com.sinergia.database.Fields;

import javafx.util.Pair;

import java.util.LinkedHashMap;
import java.util.Objects;

public class Campos {

    private int idTabela;
    private String nomeTabela;
    private int idCampo;
    private String nomeCampo;
    private String pesqCampo;
    private String descrComplCampo;
    private FieldType tipoCampo;
    private int tamCampo;
    private Boolean nullable;
    private LinkedHashMap<String, String> opcoes = new LinkedHashMap<>();

    public Campos(int idTabela, String nomeTabela, int idCampo, String nomeCampo, String pesqCampo, String descrComplCampo, FieldType tipoCampo, int tamCampo, Boolean nullable) {
        super();
        this.idTabela = idTabela;
        this.nomeTabela = nomeTabela;
        this.idCampo = idCampo;
        this.nomeCampo = nomeCampo;
        this.pesqCampo = pesqCampo;
        this.descrComplCampo = descrComplCampo;
        this.tipoCampo = tipoCampo;
        this.tamCampo = tamCampo;
        this.nullable = nullable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campos campos = (Campos) o;
        return idTabela == campos.idTabela &&
                idCampo == campos.idCampo &&
                tamCampo == campos.tamCampo &&
                Objects.equals(nomeTabela, campos.nomeTabela) &&
                Objects.equals(nomeCampo, campos.nomeCampo) &&
                Objects.equals(pesqCampo, campos.pesqCampo) &&
                Objects.equals(descrComplCampo, campos.descrComplCampo) &&
                tipoCampo == campos.tipoCampo &&
                Objects.equals(nullable, campos.nullable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTabela, nomeTabela, idCampo, nomeCampo, pesqCampo, descrComplCampo, tipoCampo, tamCampo, nullable);
    }

    @Override
    public String toString() {
        return "Campos{" +
                "idTabela=" + idTabela +
                ", nomeTabela='" + nomeTabela + '\'' +
                ", idCampo=" + idCampo +
                ", nomeCampo='" + nomeCampo + '\'' +
                ", pesqCampo='" + pesqCampo + '\'' +
                ", descrComplCampo='" + descrComplCampo + '\'' +
                ", tipoCampo=" + tipoCampo +
                ", tamCampo=" + tamCampo +
                ", nullable=" + nullable +
                '}';
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

    public int getIdCampo() {
        return idCampo;
    }

    public void setIdCampo(int idCampo) {
        this.idCampo = idCampo;
    }

    public String getNomeCampo() {
        return nomeCampo;
    }

    public void setNomeCampo(String nomeCampo) {
        this.nomeCampo = nomeCampo;
    }

    public String getPesqCampo() {
        return pesqCampo;
    }

    public void setPesqCampo(String pesqCampo) {
        this.pesqCampo = pesqCampo;
    }

    public String getDescrComplCampo() {
        return descrComplCampo;
    }

    public void setDescrComplCampo(String descrComplCampo) {
        this.descrComplCampo = descrComplCampo;
    }

    public FieldType getTipoCampo() {
        return tipoCampo;
    }

    public void setTipoCampo(FieldType tipoCampo) {
        this.tipoCampo = tipoCampo;
    }

    public int getTamCampo() {
        return tamCampo;
    }

    public void setTamCampo(int tamCampo) {
        this.tamCampo = tamCampo;
    }

    public Boolean isNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public LinkedHashMap<String, String> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(LinkedHashMap<String, String> opcoes) {
        this.opcoes = opcoes;
    }

    public Boolean hasOpcoes() {
        if(getOpcoes() != null && getOpcoes().size() > 0) return true;
        else return false;
    }
}
