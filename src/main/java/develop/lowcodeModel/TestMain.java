package develop.lowcodeModel;

import develop.lowcodeModel.coderModel.FileSourceMethod;
import develop.lowcodeModel.coderModel.MapTransformFunction;
import develop.lowcodeModel.coderModel.SourceMethod;
import develop.lowcodeModel.coderModel.TransformMethod;
import develop.lowcodeModel.modelBean.DevelopModelClass;
import develop.lowcodeModel.modelBuilder.DevelopClassBuilder;
import develop.lowcodeModel.utils.GenericTypes;
import develop.lowcodeModel.utils.ScopeTypes;
import develop.lowcodeModel.utils.SymbolTypes;
import jdk.nashorn.internal.ir.Symbol;

import java.util.ArrayList;
import java.util.List;

public class TestMain {
    public static void main(String[] args) {

        String inputClassType = "String";
        String outPutClassType = "Integer";
        String functionBody = "s.length()";
        String functionName = "develop_map";
        String inputParamName = "ds";


        MapTransformFunction.MapTransformFunctionBuilder mapBuilder = new MapTransformFunction.MapTransformFunctionBuilder();
        MapTransformFunction mapF = mapBuilder.inputClassType(inputClassType)
                .outPutClassType(outPutClassType)
                .functionBody(functionBody)
                .functionName(functionName)
                .inputParamName(inputParamName)
                .build();


        String developClassName = "DevelopMainTest";
        String filePath = "X:\\IdeaProjects\\FlinkTest\\src\\main\\resources\\sourceTest2.txt";

        List<SourceMethod> sourceMethodList = new ArrayList<>();
        sourceMethodList.add(new FileSourceMethod(filePath));

        List<TransformMethod> transformMethodList = new ArrayList<>();
        transformMethodList.add(mapF);


        DevelopClassBuilder classBuilder = new DevelopClassBuilder();
        DevelopModelClass developModelClass = classBuilder.isMainClass(true)
                .developClassName(developClassName)
                .sourceMethods(sourceMethodList)
                .transformMethods(transformMethodList)
                .build();



        System.out.println(developModelClass.getClassCode());
    }
}
