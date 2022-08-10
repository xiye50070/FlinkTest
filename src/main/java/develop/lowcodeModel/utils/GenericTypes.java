package develop.lowcodeModel.utils;



public enum GenericTypes {
    Trans_Integer("Integer"),
    Trans_String("String");

    private String typeString;




    GenericTypes(String typeString) {
        this.typeString = typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }

}
