package br.com.sinergia.database.Fields;

public enum FieldType {

    BOOLEAN(1), NUMBER(2), FLOAT(3), STRING(4), DATE(5), BLOB(6);

    private final int valor;

    FieldType(int valorOpcao) {
        valor = valorOpcao;
    }

    public static FieldType getType(int idx) {
        FieldType type;
        switch (idx) {
            case 1:
                type = BOOLEAN;
                break;
            case 2:
                type = NUMBER;
                break;
            case 3:
                type = FLOAT;
                break;
            case 4:
                type = STRING;
                break;
            case 5:
                type = DATE;
                break;
            case 6:
                type = BLOB;
                break;
            default:
                System.err.println("Erro");
                type = null;
                break;
        }
        return type;
    }

    public int getValor() {
        return valor;
    }
}
