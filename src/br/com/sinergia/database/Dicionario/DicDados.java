package br.com.sinergia.database.Dicionario;

import br.com.sinergia.database.Fields.Campos;
import br.com.sinergia.database.Fields.FieldType;
import br.com.sinergia.database.Fields.Tabelas;
import br.com.sinergia.functions.functions;
import javafx.application.Platform;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;

public class DicDados {

    private static final URL urlXML = DicDados.class.getResource("/br/com/sinergia/database/Dicionario/DicionarioDados.xml");
    private static Boolean estruturado = false;
    private static ArrayList<Tabelas> tabelas = new ArrayList<>();

    public static void readDicionario() throws Error {
        Platform.runLater(() -> {
            if (!estruturado) {
                try {
                    DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
                    Document doc = docBuilder.parse(urlXML.getPath());
                    doc.getDocumentElement().normalize();
                    NodeList nodeTelas = doc.getElementsByTagName("Tabela");
                    int qtdTelas = nodeTelas.getLength();
                    for (int idx = 0; idx < qtdTelas; idx++) {
                        readDicionarioTable((Element) nodeTelas.item(idx));
                    }
                    estruturado = true;
                } catch (Exception ex) {
                    throw new Error(ex);
                }
            }
        });
    }

    private static void readDicionarioTable(Element element) throws Error {
        try {
            Tabelas tabela;
            int idTabela = Integer.valueOf(element.getAttribute("id"));
            String nomeTabela = element.getAttribute("nome");
            String pesqTabela = element.getAttribute("pesq");
            String descrComplTabela = element.getAttribute("descrCompl");
            tabela = new Tabelas(idTabela, nomeTabela, pesqTabela, descrComplTabela);
            NodeList nodeCampos = element.getElementsByTagName("Campo");
            int qtdCampos = nodeCampos.getLength();
            ArrayList<Campos> camposTabela = new ArrayList<>();
            for (int idx = 0; idx < qtdCampos; idx++) {
                camposTabela.add(readDicionarioCampos(tabela, (Element) nodeCampos.item(idx)));
            }
            tabela.setTabCampos(camposTabela);
            getTabelas().add(tabela);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    private static Campos readDicionarioCampos(Tabelas tabela, Element element) throws Error {
        try {
            Campos campo;
            int idCampo = Integer.valueOf(element.getAttribute("id"));
            String nomeCampo = element.getAttribute("nome");
            String pesqCampo = element.getAttribute("pesq");
            String descrComplCampo = element.getAttribute("descrCompl");
            FieldType tipoCampo = FieldType.getType(Integer.valueOf(element.getAttribute("tipo")));
            int tamCampo = Integer.valueOf(element.getAttribute("tam"));
            Boolean nullable = functions.toBoo(element.getAttribute("nullable"));
            campo = new Campos(tabela.getIdTabela(), tabela.getNomeTabela(),
                    idCampo, nomeCampo, pesqCampo, descrComplCampo, tipoCampo, tamCampo, nullable);
            return campo;
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public static ArrayList<Tabelas> getTabelas() {
        return tabelas;
    }

    public static void setTabelas(ArrayList<Tabelas> tabelas) {
        DicDados.tabelas = tabelas;
    }
}
