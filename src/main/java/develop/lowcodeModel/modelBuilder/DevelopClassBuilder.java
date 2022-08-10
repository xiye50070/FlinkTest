package develop.lowcodeModel.modelBuilder;

import develop.lowcodeModel.coderModel.SinkMethod;
import develop.lowcodeModel.coderModel.SourceMethod;
import develop.lowcodeModel.coderModel.TransformMethod;
import develop.lowcodeModel.modelBean.DevelopModelClass;

import java.util.List;

public class DevelopClassBuilder implements BaseBuilder<DevelopModelClass>{

    String developClassName;

    boolean isMainClass;

    List<SourceMethod> sourceMethods;

    List<TransformMethod> transformMethods;

    List<SinkMethod> sinkMethods;

    public DevelopClassBuilder developClassName(String developClassName){
        this.developClassName = developClassName;
        return this;
    }

    public DevelopClassBuilder isMainClass(boolean isMainClass){
        this.isMainClass = isMainClass;
        return this;
    }

    public DevelopClassBuilder sourceMethods(List<SourceMethod> sourceMethods){
        this.sourceMethods = sourceMethods;
        return this;
    }

    public DevelopClassBuilder transformMethods(List<TransformMethod> transformMethods){
        this.transformMethods = transformMethods;
        return this;
    }

    public DevelopClassBuilder sinkMethods(List<SinkMethod> sinkMethods){
        this.sinkMethods = sinkMethods;
        return this;
    }

    @Override
    public DevelopModelClass build() {
        return new DevelopModelClass(developClassName,isMainClass,sourceMethods,transformMethods,sinkMethods);
    }
}
