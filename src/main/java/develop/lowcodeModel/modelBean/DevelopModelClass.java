package develop.lowcodeModel.modelBean;

import develop.lowcodeModel.coderModel.CommonCodeModel;
import develop.lowcodeModel.coderModel.SinkMethod;
import develop.lowcodeModel.coderModel.SourceMethod;
import develop.lowcodeModel.coderModel.TransformMethod;
import develop.lowcodeModel.utils.ScopeTypes;
import develop.lowcodeModel.utils.SymbolTypes;

import java.util.List;

public class DevelopModelClass implements BaseModel{

    String className;

    boolean isMainClass;

    List<SourceMethod> sourceMethods;

    List<TransformMethod> transformMethods;

    List<SinkMethod> sinkMethods;

    String classCode;

    @Override
    public void build() {
        StringBuilder classCodeText = new StringBuilder();
        classCodeText.append(ScopeTypes.PUBLIC.getTypeString()).append(SymbolTypes.SPACE.getSymbolString())
                .append("class").append(SymbolTypes.SPACE.getSymbolString())
                .append(className).append(SymbolTypes.SPACE.getSymbolString())
                .append(SymbolTypes.BRANCE_LEFT.getSymbolString()).append(SymbolTypes.ENTER.getSymbolString());

        if (isMainClass){
            classCodeText.append(CommonCodeModel.MAIN_METHOD).append(SymbolTypes.ENTER.getSymbolString())
                    .append(CommonCodeModel.STREAM_ENV).append(SymbolTypes.ENTER.getSymbolString());
        }

        if (sourceMethods.isEmpty() || transformMethods.isEmpty()){
            throw new IllegalArgumentException("Source、Transform 不可为空");
        }
        sourceMethods.forEach(sourceMethod -> classCodeText.append(sourceMethod.buildSourceCode()).append(SymbolTypes.ENTER.getSymbolString()));
        transformMethods.forEach(transformMethod -> classCodeText.append(transformMethod.buildTransformMethodCode()).append(SymbolTypes.ENTER.getSymbolString()));
        if (sinkMethods!=null && !sinkMethods.isEmpty()){
            sinkMethods.forEach(sinkMethod -> classCodeText.append(sinkMethod).append(SymbolTypes.ENTER.getSymbolString()));
        }


        if (isMainClass){
            classCodeText.append(CommonCodeModel.EXECUTE).append(SymbolTypes.ENTER.getSymbolString())
                    .append(SymbolTypes.BRANCE_RIGHT.getSymbolString()).append(SymbolTypes.ENTER.getSymbolString());
        }
        classCodeText.append(SymbolTypes.BRANCE_RIGHT.getSymbolString());

        this.classCode = classCodeText.toString();
    }

    public DevelopModelClass(String className, boolean isMainClass, List<SourceMethod> sourceMethods,
                             List<TransformMethod> transformMethods, List<SinkMethod> sinkMethods) {
        this.className = className;
        this.isMainClass = isMainClass;
        this.sourceMethods = sourceMethods;
        this.transformMethods = transformMethods;
        this.sinkMethods = sinkMethods;
        this.build();
    }

    public DevelopModelClass() {
    }

    public String getClassCode() {
        return classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isMainClass() {
        return isMainClass;
    }

    public void setMainClass(boolean mainClass) {
        isMainClass = mainClass;
    }

    public List<SourceMethod> getSourceMethods() {
        return sourceMethods;
    }

    public void setSourceMethods(List<SourceMethod> sourceMethods) {
        this.sourceMethods = sourceMethods;
    }

    public List<TransformMethod> getTransformMethods() {
        return transformMethods;
    }

    public void setTransformMethods(List<TransformMethod> transformMethods) {
        this.transformMethods = transformMethods;
    }

    public List<SinkMethod> getSinkMethods() {
        return sinkMethods;
    }

    public void setSinkMethods(List<SinkMethod> sinkMethods) {
        this.sinkMethods = sinkMethods;
    }


}
