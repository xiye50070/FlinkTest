package develop.lowcodeModel.coderModel;

import develop.lowcodeModel.modelBean.DevelopModelFunction;
import develop.lowcodeModel.modelBuilder.MethodBuillder;
import develop.lowcodeModel.utils.GenericTypes;
import develop.lowcodeModel.utils.ScopeTypes;

import java.util.HashMap;
import java.util.Map;

public class MapTransformFunction implements TransformMethod{
    String inputClassType;

    String outPutClassType;

    String functionBody;

    String functionName;

    String inputParamName;

    private MapTransformFunction(String inputClassType, String outPutClassType, String functionBody, String functionName,String inputParamName) {
        this.inputClassType = inputClassType;
        this.outPutClassType = outPutClassType;
        this.functionBody = functionBody;
        this.functionName = functionName;
        this.inputParamName = inputParamName;
    }

    /**
     *     public DataStream<Integer> mapStream(DataStream<String> ds){
     *         return ds.map((MapFunction<String, Integer>) s -> s.length());
     *     }
     * @return
     */

    @Override
    public String buildTransformMethodCode() {
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("DataStream<String>","ds");

        MethodBuillder methodBuillder = new MethodBuillder();
        DevelopModelFunction dmf = methodBuillder
                .functionName(functionName)
                .scopeTypes(ScopeTypes.PUBLIC)
                .inParameterMap(paraMap)
                .methodBody(this.buildFunctionDetails())
                .returnType(GenericTypes.Trans_String.getTypeString())
                .build();
        return dmf.getFunctionCodes();
    }

    public String buildFunctionDetails(){
        return "return " + inputParamName + ".map((MapFunction<" + inputClassType + ", " + outPutClassType + ">) s -> " + functionBody + ");";
    }

    public static final class MapTransformFunctionBuilder{

        private String inputClassType;

        private String outPutClassType;

        private String functionBody;

        private String functionName;

        private String inputParamName;

        public MapTransformFunctionBuilder inputClassType(String inputClassType){
            this.inputClassType = inputClassType;
            return this;
        }

        public MapTransformFunctionBuilder outPutClassType(String outPutClassType){
            this.outPutClassType = outPutClassType;
            return this;
        }

        public MapTransformFunctionBuilder functionBody(String functionBody){
            this.functionBody = functionBody;
            return this;
        }

        public MapTransformFunctionBuilder functionName(String functionName){
            this.functionName = functionName;
            return this;
        }

        public MapTransformFunctionBuilder inputParamName(String inputParamName){
            this.inputParamName = inputParamName;
            return this;
        }

        public MapTransformFunction build(){
            return new MapTransformFunction(this.inputClassType,
                    this.outPutClassType,
                    this.functionBody,
                    this.functionName,
                    this.inputParamName);
        }

    }
}
