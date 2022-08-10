package develop.lowcodeModel.utils;

public enum ScopeTypes {
    PUBLIC("public"),
    PRIVATE("private"),
    PROTECT("protect"),
    DEFAULT("");

    private String typeString;

    ScopeTypes(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

}
