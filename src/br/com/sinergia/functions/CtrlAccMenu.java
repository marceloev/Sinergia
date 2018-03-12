package br.com.sinergia.functions;

import br.com.sinergia.database.conector.DBConn;
import br.com.sinergia.functions.frames.Tela;
import br.com.sinergia.functions.frames.Telas;
import br.com.sinergia.models.extendeds.ButtonTela;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelException;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.text.Normalizer;
import java.util.*;

import static br.com.sinergia.functions.functions.*;

public class CtrlAccMenu {

    public static List<String> favArquivoIni = new ArrayList<>();
    private Map<String, ArrayList<Tela>> mapMenu = new LinkedHashMap<>();
    private DBConn conex;
    private Accordion accordion;
    private String pesquisa;

    public CtrlAccMenu(Accordion accMenu) {
        String listaArquivo = nvl(CtrlArquivos.busca(User.getCurrent().getCodUsu(), "Telas Favoritas"));
        favArquivoIni = Arrays.asList(listaArquivo.split(";"));
        this.accordion = accMenu;
        this.pesquisa = "";
        loadMenuTelas();
    }

    private void loadMenuTelas() {
        accordion.getPanes().clear();
        try {
            for (String menu : Telas.getMenus()) {
                ArrayList<Tela> listTelasFromMenu = Telas.getByMenu(menu);
                getAcessosMenu(listTelasFromMenu, User.getCurrent().getCodPerfil());
                if (!listTelasFromMenu.isEmpty()) mapMenu.put(menu, Telas.getByMenu(menu));
            }
            TitledPane[] ttpMenu = new TitledPane[mapMenu.size()];
            int idx = -1;
            for (String menu : mapMenu.keySet()) {
                idx++;
                VBox vBox = new VBox();
                vBox.setSpacing(2);
                ttpMenu[idx] = new TitledPane(menu, vBox);
                ImageView IconeMenu = new ImageView("/br/com/sinergia/views/images/" + menu + ".png");
                IconeMenu.setFitHeight(32); //Redimensiona a imagem para 32x32
                IconeMenu.setFitWidth(32);
                ttpMenu[idx].setGraphic(IconeMenu);
                for (Tela tela : mapMenu.get(menu)) {
                    //Os eventos do botão, estão na classe ButtonTela;
                    ButtonTela BtnTela = new ButtonTela(tela.getCodTela(), tela.getDescrTela(), tela.getPathTela(), tela.isFrame(), tela.getFounder());
                    vBox.getChildren().add(BtnTela);
                }
            }
            accordion.getPanes().addAll(ttpMenu);
        } catch (Error ex) {
            accordion.getPanes().clear();
            ModelException.setNewException(new ModelException(this.getClass(), null, ex.getMessage()));
            ModelException.getDialog().raise();
        } catch (Exception ex) {
            accordion.getPanes().clear();
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar mapear acessos do usuário\n" + ex.getMessage(), ex));
            ModelException.getDialog().raise();
        } finally {
            conex.desconecta();
        }
    }

    public void searchFrame(String pesquisa) {
        this.pesquisa = nvl(pesquisa).replaceAll(" ", "");
        accordion.getPanes().clear();
        if (pesquisa.equals("")) {
            TitledPane[] ttpMenu = new TitledPane[mapMenu.size()];
            int idx = -1;
            for (String menu : mapMenu.keySet()) {
                idx++;
                VBox vBox = new VBox();
                vBox.setSpacing(2);
                ttpMenu[idx] = new TitledPane(menu, vBox);
                ImageView IconeMenu = new ImageView("/br/com/sinergia/views/images/" + menu + ".png");
                IconeMenu.setFitHeight(32); //Redimensiona a imagem para 32x32
                IconeMenu.setFitWidth(32);
                ttpMenu[idx].setGraphic(IconeMenu);
                for (Tela tela : mapMenu.get(menu)) {
                    //Os eventos do botão, estão na classe ButtonTela;
                    ButtonTela BtnTela = new ButtonTela(tela.getCodTela(), tela.getDescrTela(), tela.getPathTela(), tela.isFrame(), tela.getFounder());
                    vBox.getChildren().add(BtnTela);
                }
            }
            accordion.getPanes().addAll(ttpMenu);
        } else {
            this.pesquisa = translate(this.pesquisa);
            ArrayList<Tela> listTelas = new ArrayList<>();
            for (ArrayList<Tela> telas : mapMenu.values()) {
                telas.forEach(tela -> {
                    if (translate(tela.getDescrTela()).contains(this.pesquisa) && listTelas.size() <= 10)
                        listTelas.add(tela);
                });
            }
            if (listTelas.size() != 0) {
                VBox vBox = new VBox();
                vBox.setSpacing(2);
                TitledPane ttpSearch = new TitledPane("Pesquisando: " + pesquisa, vBox);
                ImageView icone = new ImageView("/br/com/sinergia/views/images/Icone_Pesquisa.png");
                icone.setFitHeight(25);
                icone.setFitWidth(25);
                ttpSearch.setGraphic(icone);
                for (Tela tela : listTelas) {
                    ButtonTela BtnTela = new ButtonTela(tela.getCodTela(), tela.getDescrTela(), tela.getPathTela(), tela.isFrame(), tela.getFounder());
                    vBox.getChildren().add(BtnTela);
                }
                accordion.getPanes().add(ttpSearch);
                accordion.getPanes().get(0).setExpanded(true);
                accordion.getPanes().get(0).setCollapsible(false);
            }
        }
    }

    private void getAcessosMenu(ArrayList<Tela> arrayTelas, Integer codPerfil) throws SQLException, Error {
        ArrayList<Integer> codTelas = new ArrayList<>();
        arrayTelas.forEach(tela -> {
            codTelas.add(tela.getCodTela());
        });
        conex = new DBConn(this.getClass(), false,
                "SELECT CODTELA, VISUALIZA FROM TSIPER WHERE CODTELA IN " + arrayParameter(codTelas) + " AND CODPERFIL = ?");
        conex.addParameter(codTelas);
        conex.addParameter(codPerfil);
        conex.createSet();
        while (conex.rs.next()) {
            Boolean visualiza = toBoo(conex.rs.getString(2));
            int tela = conex.rs.getInt(1);
            if (!visualiza) {
                arrayTelas.remove(Telas.getByCod(tela));
                codTelas.remove(tela);
            }
        }
    }

    private String translate(String valor) {
        String retorno = valor.replaceAll(" ", "");
        retorno = Normalizer.normalize(retorno, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        retorno = retorno.toUpperCase();
        retorno = retorno.replace("/^[\\.-]/", "");
        return retorno;
    }

}
