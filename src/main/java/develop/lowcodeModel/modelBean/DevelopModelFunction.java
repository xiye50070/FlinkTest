package develop.lowcodeModel.modelBean;

import develop.lowcodeModel.utils.ScopeTypes;
import develop.lowcodeModel.utils.SymbolTypes;

import java.util.*;
import java.util.stream.Collectors;

public class DevelopModelFunction implements BaseModel{

    String functionName;

    ScopeTypes scopeTypes;

    String returnType;

    Map<String,String> inParameterMap;

    String methodBody;

    String functionCodes;
    @Override
    public void build(){
        StringBuilder codeTextBuilder = new StringBuilder();
        codeTextBuilder
                // 方法范围
                .append(scopeTypes.getTypeString()).append(SymbolTypes.SPACE.getSymbolString())
                // 方法返回类型
                .append(returnType).append(SymbolTypes.SPACE.getSymbolString())
                // 方法名
                .append(functionName).append(SymbolTypes.PARENTHESIS_LEFT.getSymbolString());

        // 方法入参
        if (!inParameterMap.isEmpty()){
            List<Map.Entry<String, String>> collect = new ArrayList<>(inParameterMap.entrySet());
            for (int i = 0; i < collect.size(); i++) {
                Map.Entry<String, String> map = collect.get(i);
                codeTextBuilder.append(map.getKey()).append(SymbolTypes.SPACE.getSymbolString()).append(map.getValue());
                // 确保不以逗号结尾
                if (i!=collect.size()-1){
                    codeTextBuilder.append(SymbolTypes.COMMA.getSymbolString());
                }else {
                    // 结尾补充括号和空格
                    codeTextBuilder.append(SymbolTypes.PARENTHESIS_RIGHT.getSymbolString()).append(SymbolTypes.SPACE.getSymbolString());
                }
            }
        }

        // 方法体
        codeTextBuilder.append(SymbolTypes.BRANCE_LEFT.getSymbolString()).append(SymbolTypes.ENTER.getSymbolString())
                .append(methodBody).append(SymbolTypes.ENTER.getSymbolString()).append(SymbolTypes.BRANCE_RIGHT.getSymbolString());

        this.functionCodes = codeTextBuilder.toString();
    }


    public DevelopModelFunction(String functionName, ScopeTypes scopeTypes, String returnType, Map<String, String> inParameterMap, String methodBody) {
        this.functionName = functionName;
        this.scopeTypes = scopeTypes;
        this.returnType = returnType;
        this.inParameterMap = inParameterMap;
        this.methodBody = methodBody;

        this.build();
    }

    public DevelopModelFunction() {
    }

    public String getFunctionName() {
        return functionName;
    }

    public ScopeTypes getScopeTypes() {
        return scopeTypes;
    }

    public String getReturnType() {
        return returnType;
    }

    public Map<String, String> getInParameterMap() {
        return inParameterMap;
    }

    public String getMethodBody() {
        return methodBody;
    }

    public String getFunctionCodes() {
        return functionCodes;
    }
}
