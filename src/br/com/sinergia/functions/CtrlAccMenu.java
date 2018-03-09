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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static br.com.sinergia.functions.functions.*;

public class CtrlAccMenu {

    DBConn conex;
    private Accordion accordion;
    public static String favArquivoIni = CtrlArquivos.busca(User.getCurrent().getCodUsu(), "Telas Favoritas");

    public CtrlAccMenu(Accordion accMenu) {
        accordion = accMenu;
        loadMenuTelas();
    }

    private void loadMenuTelas() {
        try {
            Map<String, ArrayList<Tela>> mapMenu = new LinkedHashMap<>();
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
                vBox.setSpacing(1);
                ttpMenu[idx] = new TitledPane(menu, vBox);
                ImageView IconeMenu = new ImageView("/br/com/sinergia/views/images/" + menu + ".png");
                IconeMenu.setFitHeight(32); //Redimensiona a imagem para 32x32
                IconeMenu.setFitWidth(32);
                ttpMenu[idx].setGraphic(IconeMenu);
                for (Tela tela : mapMenu.get(menu)) {
                    ButtonTela BtnTela = new ButtonTela(tela.getCodTela(), tela.getDescrTela(), tela.getPathTela(), tela.isFrame(), tela.getFounder());
                    BtnTela.setText(tela.getDescrTela()); //Os eventos do botão, estão na classe ButtonTela;
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

    private void getAcessosMenu(ArrayList<Tela> arrayTelas, Integer codPerfil) throws SQLException, Error {
        ArrayList<Integer> codTelas = new ArrayList<>();
        ArrayList<String> nomeTelasSearch = new ArrayList<>();
        ArrayList<String> nomeTelasNotFound = new ArrayList<>();
        int index = 0;
        arrayTelas.forEach(tela -> {
            codTelas.add(tela.getCodTela());
            nomeTelasSearch.add(tela.getDescrTela());
            nomeTelasNotFound.add(tela.getDescrTela());
        });
        conex = new DBConn(this.getClass(), true,
                "SELECT VISUALIZA FROM TSIPER WHERE CODTELA IN " + arrayParameter(codTelas) + " AND CODPERFIL = ?");
        conex.addParameter(codTelas);
        conex.addParameter(codPerfil);
        conex.createSet();
        while (conex.rs.next()) {
            Boolean visualiza = toBoo(conex.rs.getString(1));
            if (!visualiza) arrayTelas.remove(index);
            nomeTelasNotFound.remove(index);
            index++;
        }
        if (index != codTelas.size()) {
            throw new Error("Erro ao tentar mapear acessos do usuário\n" +
                    "Foram buscadas: " + nomeTelasSearch.toString() + "\n" +
                    "Não foram encontradas: " + nomeTelasNotFound.toString());
        }
    }

}
