package br.com.sinergia.models.usage;

import javafx.scene.image.Image;

import java.sql.Timestamp;

public class Mensagem {

    private int codMsg;
    private int codUsu;
    private String loginUsu;
    private Timestamp dhAlter;
    private int prioridade;
    private String titulo;
    private String mensagem;
    private Boolean visualizada;
    private Image imgUsu;

    public Mensagem(int codMsg, int codUsu, String loginUsu, Timestamp dhAlter, int prioridade, String titulo, String mensagem, Boolean visualizada, Image imgUsu) {
        super();
        setCodMsg(codMsg);
        setCodUsu(codUsu);
        setLoginUsu(loginUsu);
        setDhAlter(dhAlter);
        setPrioridade(prioridade);
        setTitulo(titulo);
        setMensagem(mensagem);
        setVisualizada(visualizada);
        setImgUsu(imgUsu);
    }

    public int getCodUsu() {
        return codUsu;
    }

    public void setCodUsu(int codUsu) {
        this.codUsu = codUsu;
    }

    public String getLoginUsu() {
        return loginUsu;
    }

    public void setLoginUsu(String loginUsu) {
        this.loginUsu = loginUsu;
    }

    public Timestamp getDhAlter() {
        return dhAlter;
    }

    public void setDhAlter(Timestamp dhAlter) {
        this.dhAlter = dhAlter;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Image getImgUsu() {
        return imgUsu;
    }

    public void setImgUsu(Image imgUsu) {
        if(imgUsu == null) this.imgUsu = new Image("/br/com/sinergia/views/images/default.png");
        else this.imgUsu = imgUsu;
    }

    public int getCodMsg() {
        return codMsg;
    }

    public void setCodMsg(int codMsg) {
        this.codMsg = codMsg;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public Boolean getVisualizada() {
        return visualizada;
    }

    public void setVisualizada(Boolean visualizada) {
        this.visualizada = visualizada;
    }
}
