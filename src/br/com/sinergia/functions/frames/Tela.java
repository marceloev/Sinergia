package br.com.sinergia.functions.frames;

public class Tela {
    private Integer codTela;
    private String descrTela;
    private String pathTela;
    private Boolean Frame;
    private String founder;

    public Tela(Integer codTela, String descrTela, String pathTela, Boolean isFrame, String founder) {
        super();
        setCodTela(codTela);
        setDescrTela(descrTela);
        setPathTela(pathTela);
        setFrame(isFrame);
        setFounder(founder);
    }

    public Integer getCodTela() {
        return codTela;
    }

    public void setCodTela(Integer codTela) {
        this.codTela = codTela;
    }

    public String getDescrTela() {
        return descrTela;
    }

    public void setDescrTela(String descrTela) {
        this.descrTela = descrTela;
    }

    public String getPathTela() {
        return pathTela;
    }

    public void setPathTela(String pathTela) {
        this.pathTela = pathTela;
    }

    public Boolean isFrame() {
        return Frame;
    }

    public void setFrame(Boolean frame) {
        this.Frame = frame;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }
}
