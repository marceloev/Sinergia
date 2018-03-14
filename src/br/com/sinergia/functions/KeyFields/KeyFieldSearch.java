package br.com.sinergia.functions.KeyFields;

import br.com.sinergia.database.Dicionario.DicDados;
import br.com.sinergia.database.Fields.Campos;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class KeyFieldSearch {

    private ImageView imgSearch;
    private ArrayList<Campos> camposShow = new ArrayList<>();
    private ArrayList<Campos> camposFilter = new ArrayList<>();
    private String query;

    public KeyFieldSearch(ImageView imgSearch, ArrayList<Campos> camposFilter, String query) {
        super();
        //new Campos[]{DicDados.getTabelas().get(0).getTabCampos().get(1), DicDados.getTabelas().get(1).getTabCampos().get(1)};
        this.imgSearch = imgSearch;
        this.camposFilter = camposFilter;
        this.query = query;
        //createStage o stage para economizar memória, pode ser um estático.
        addEventOnImage(imgSearch);
    }

    private void addEventOnImage(ImageView imgView) {
        imgView.setOnMouseClicked(e -> {

        });
    }

    public ImageView getImgSearch() {
        return imgSearch;
    }

    public void setImgSearch(ImageView imgSearch) {
        this.imgSearch = imgSearch;
    }

    public ArrayList<Campos> getCamposFilter() {
        return camposFilter;
    }

    public void setCamposFilter(ArrayList<Campos> camposFilter) {
        this.camposFilter = camposFilter;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<Campos> getCamposShow() {
        return camposShow;
    }

    public void setCamposShow(ArrayList<Campos> camposShow) {
        this.camposShow = camposShow;
    }
}
