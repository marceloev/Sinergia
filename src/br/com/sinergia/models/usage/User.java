package br.com.sinergia.models.usage;

import javafx.scene.image.Image;

import java.sql.Timestamp;
import java.time.Instant;

public class User {

    private static User user;

    private int codUsu;
    private int codSessão;
    private int codPerfil;
    private int codEmp;
    private Boolean ativo;
    private String loginUsu;
    private String nomeUsu;
    private String senhaUsu;
    private String cryptSenha;
    private String nomeFantasiaEmp;
    private String razaoSocialEmp;
    private String cnpj;
    private Timestamp dhLogin;
    private Image fotoUsu;

    public User(int codUsu, Boolean ativo, String loginUsu, String nomeUsu, Image fotoUsu, int codPerfil, String senhaUsu, String cryptSenha,
                int codEmp, String nomeFantasiaEmp, String razaoSocialEmp, String cnpj) {
        setCodUsu(codUsu);
        setAtivo(ativo);
        setLoginUsu(loginUsu);
        setNomeUsu(nomeUsu);
        setFotoUsu(fotoUsu);
        setCodPerfil(codPerfil);
        setSenhaUsu(senhaUsu);
        setCryptSenha(cryptSenha);
        setCodEmp(codEmp);
        setNomeFantasiaEmp(nomeFantasiaEmp);
        setRazaoSocialEmp(razaoSocialEmp);
        setCnpj(cnpj);
        setDhLogin(Timestamp.from(Instant.now()));
    }

    public static User getCurrent() {
        return user;
    }

    public static void setCurrent(User user) {
        User.user = user;
    }

    public int getCodUsu() {
        return codUsu;
    }

    public void setCodUsu(int codUsu) {
        this.codUsu = codUsu;
    }

    public int getCodSessão() {
        return codSessão;
    }

    public void setCodSessão(int codSessão) {
        this.codSessão = codSessão;
    }

    public int getCodPerfil() {
        return codPerfil;
    }

    public void setCodPerfil(int codPerfil) {
        this.codPerfil = codPerfil;
    }

    public int getCodEmp() {
        return codEmp;
    }

    public void setCodEmp(int codEmp) {
        this.codEmp = codEmp;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getLoginUsu() {
        return loginUsu;
    }

    public void setLoginUsu(String loginUsu) {
        this.loginUsu = loginUsu;
    }

    public String getNomeUsu() {
        return nomeUsu;
    }

    public void setNomeUsu(String nomeUsu) {
        this.nomeUsu = nomeUsu;
    }

    public String getSenhaUsu() {
        return senhaUsu;
    }

    public void setSenhaUsu(String senhaUsu) {
        this.senhaUsu = senhaUsu;
    }

    public String getCryptSenha() {
        return cryptSenha;
    }

    public void setCryptSenha(String cryptSenha) {
        this.cryptSenha = cryptSenha;
    }

    public String getNomeFantasiaEmp() {
        return nomeFantasiaEmp;
    }

    public void setNomeFantasiaEmp(String nomeFantasiaEmp) {
        this.nomeFantasiaEmp = nomeFantasiaEmp;
    }

    public String getRazaoSocialEmp() {
        return razaoSocialEmp;
    }

    public void setRazaoSocialEmp(String razaoSocialEmp) {
        this.razaoSocialEmp = razaoSocialEmp;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Timestamp getDhLogin() {
        return dhLogin;
    }

    public void setDhLogin(Timestamp dhLogin) {
        this.dhLogin = dhLogin;
    }

    public Image getFotoUsu() {
        return fotoUsu;
    }

    public void setFotoUsu(Image fotoUsu) {
        this.fotoUsu = fotoUsu;
    }
}