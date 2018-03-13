package br.com.sinergia.database.querys;

public class ListaQuerys {

    public static String getAgrListCodUsu() {
        return "SELECT LISTAGG(USU.CODUSU, ';') WITHIN GROUP (ORDER BY USU.CODUSU) FROM TSIUSU USU";
    }

    public static String getQueryInsertLembrete() {
        return "INSERT INTO TRIMSG (CODMSG, CODUSU, VISUALIZADA, MENSAGEM, PRIORIDADE, DHALTER, TITULO, DHVISUALIZADA, CODUSUREM)\n" +
                "VALUES (GET_COD('CODMSG', 'TRIMSG'), ?, ?, ?, ?, ?, ?, ?, ?)";
    }
}
