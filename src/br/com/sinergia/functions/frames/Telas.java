package br.com.sinergia.functions.frames;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static br.com.sinergia.functions.functions.toBoo;

public class Telas {

    private static ArrayList<String> menus = new ArrayList<>();
    private static ArrayList<Tela> telas = new ArrayList<>();
    private static final URL urlXML = Telas.class.getResource("/br/com/sinergia/functions/frames/Telas.xml");

    public static void loadConf() throws Error {
        try {
            DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
            Document doc = docBuilder.parse(urlXML.getPath());
            doc.getDocumentElement().normalize();
            NodeList nodeTelas = doc.getElementsByTagName("Tela");
            int qtdTelas = nodeTelas.getLength();
            Element element;
            for (int idx = 0; idx < qtdTelas; idx++) {
                element = (Element) nodeTelas.item(idx);
                Integer codTela = Integer.valueOf(element.getAttribute("id"));
                String descrTela = element.getElementsByTagName("descrTela").item(0).getTextContent();
                String pathTela = element.getElementsByTagName("path").item(0).getTextContent();
                Boolean isFrame = toBoo(element.getElementsByTagName("isFrame").item(0).getTextContent());
                String founder = element.getElementsByTagName("founder").item(0).getTextContent();
                getTelas().add(new Tela(codTela, descrTela, pathTela, isFrame, founder));
                if(!getMenus().contains(pathTela)) getMenus().add(pathTela);
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new Error(ex);
        }
    }

    public static Tela getByCod(Integer cod) {
        for(Tela tela: Telas.getTelas()) {
            if(tela.getCodTela().equals(cod)) {
                return tela;
            }
        }
        return null;
    }

    public static ArrayList<Tela> getTelas() {
        return telas;
    }

    public static ArrayList<Tela> getByMenu(String menu) throws Error {
        if(getMenus().contains(menu)) {
            ArrayList<Tela> telas = new ArrayList<>();
            for(Tela tela: getTelas()) {
                if(tela.getPathTela().equals(menu)) telas.add(tela);
            }
            return telas;
        } else {
            throw new Error("Não encontrado menu com descrição igual a: " + menu);
        }
    }

    public static ArrayList<String> getMenus() {
        return menus;
    }
}
