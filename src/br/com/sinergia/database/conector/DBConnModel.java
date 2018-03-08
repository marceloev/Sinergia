package br.com.sinergia.database.conector;

public class DBConnModel {

    private Class invoker;
    private Boolean hide;
    private Boolean expires;
    private Integer timeOutExpire;

    public DBConnModel(Class invoker, Boolean hide, Boolean expires, Integer secTimeOut) {
        super();
        setInvoker(invoker);
        setHide(hide);
        setExpires(expires);
        setTimeOutExpire(secTimeOut);
    }

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    public Boolean getExpires() {
        return expires;
    }

    public void setExpires(Boolean expires) {
        this.expires = expires;
    }

    public Integer getTimeOutExpire() {
        return timeOutExpire;
    }

    public void setTimeOutExpire(Integer timeOutExpire) {
        this.timeOutExpire = timeOutExpire;
    }

    public Class getInvoker() {
        return invoker;
    }

    public void setInvoker(Class invoker) {
        this.invoker = invoker;
    }
}
