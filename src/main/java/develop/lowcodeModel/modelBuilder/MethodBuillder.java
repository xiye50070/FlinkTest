package develop.lowcodeModel.modelBuilder;

import develop.lowcodeModel.modelBean.DevelopModelFunction;
import develop.lowcodeModel.utils.ScopeTypes;

import java.util.Map;

public class MethodBuillder implements BaseBuilder<DevelopModelFunction>{

    String functionName;

    ScopeTypes scopeTypes;

    String returnType;

    Map<String,String> inParameterMap;

    String methodBody;

    public MethodBuillder functionName(String functionName){
        this.functionName = functionName;
        return this;
    }

    public MethodBuillder scopeTypes(ScopeTypes scopeTypes){
        this.scopeTypes = scopeTypes;
        return this;
    }

    public MethodBuillder returnType(String returnType){
        this.returnType = returnType;
        return this;
    }

    public MethodBuillder inParameterMap(Map<String,String> inParameterMap){
        this.inParameterMap = inParameterMap;
        return this;
    }

    public MethodBuillder methodBody(String methodBody){
        this.methodBody = methodBody;
        return this;
    }


    @Override
    public DevelopModelFunction build() {
        return new DevelopModelFunction(this.functionName, this.scopeTypes,this.returnType,this.inParameterMap,this.methodBody);
    }
}
