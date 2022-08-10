package develop.lowcodeModel.coderModel;

public class FileSourceMethod implements SourceMethod{

    String filePath;

    public FileSourceMethod(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String buildSourceCode(){
        return "DataStream<String> ds = env.readTextFile(\"" + filePath +"\");";
    }
}
