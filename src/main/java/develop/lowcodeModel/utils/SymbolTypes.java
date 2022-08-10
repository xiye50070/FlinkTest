package develop.lowcodeModel.utils;

public enum SymbolTypes {
    SPACE(" "),
    ENTER("\n"),
    COMMA(","),
    SEMICOLON(";"),

    PLACEHOLDER("    "),
    DOUBLE_PLACEHOLDER("    "),
    PARENTHESIS_LEFT("("),
    PARENTHESIS_RIGHT(")"),
    BRANCE_LEFT("{"),
    BRANCE_RIGHT("}");

    public String symbolString;


    SymbolTypes(String symbolString) {
        this.symbolString = symbolString;
    }

    public void setSymbolString(String symbolString) {
        this.symbolString = symbolString;
    }

    public String getSymbolString() {
        return symbolString;
    }
}
