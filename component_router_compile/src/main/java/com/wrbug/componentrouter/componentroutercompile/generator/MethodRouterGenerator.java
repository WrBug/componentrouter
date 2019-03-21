package com.wrbug.componentrouter.componentroutercompile.generator;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wrbug.componentrouter.ComponentRouterProxy;
import com.wrbug.componentrouter.annotation.MethodRouter;
import com.wrbug.componentrouter.componentroutercompile.MethodInfo;
import com.wrbug.componentrouter.componentroutercompile.TypeNameUtils;
import com.wrbug.componentrouter.componentroutercompile.util.Log;
import com.wrbug.componentrouter.componentroutercompile.util.MD5;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.wrbug.componentrouter.componentroutercompile.Constant.*;

public class MethodRouterGenerator extends ElementGenerator {


    public MethodRouterGenerator(Filer filer, Log log) {
        super(filer, log);
    }

    @Override
    public TypeSpec onCreateTypeSpec(TypeElement element, String packageName, String className) {
        ClassName routeType = ClassName.get(packageName, className);
        TypeSpec.Builder builder = TypeSpec.classBuilder(className + PROXY_SUFFIX)
                .addSuperinterface(ComponentRouterProxy.class)
                .addModifiers(Modifier.PUBLIC)
                .addField(routeType, PROXY_FIELD_NAME, Modifier.PRIVATE);
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        Map<String, MethodInfo> map = new HashMap<>();
        for (Element enclosedElement : enclosedElements) {
            MethodRouter annotation = enclosedElement.getAnnotation(MethodRouter.class);
            if (annotation != null) {
                MethodInfo methodInfo = new MethodInfo();
                methodInfo.setMethodName(enclosedElement.getSimpleName().toString());
                TypeMirror typeMirror = enclosedElement.asType();
                try {
                    String str = enclosedElement.toString();
                    str = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
                    if (!str.isEmpty()) {
                        String[] argTypes = str.split(",");
                        methodInfo.setArgType(argTypes);
                    }
                    Object type = typeMirror.getClass().getDeclaredField("restype").get(typeMirror);
                    methodInfo.setReturnType(type.toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                methodInfo.setDesc(annotation.javaDocDesc());
                map.put(annotation.value(), methodInfo);
            }

        }
        addConstructorMethod(builder, routeType);
        addIsForMethod(builder, routeType);
        addCallMethod(builder, className, map);
        return builder.build();
    }

    private void addConstructorMethod(TypeSpec.Builder builder, ClassName routeType) {
        MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                .addParameter(routeType, "obj")
                .addModifiers(Modifier.PUBLIC)
                .addCode(PROXY_FIELD_NAME + "=obj;\n");
        builder.addMethod(methodBuilder.build());
    }

    private void addIsForMethod(TypeSpec.Builder builder, ClassName routeType) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(METHOD_IS)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .returns(boolean.class)
                .addParameter(Object.class, "obj")
                .addCode("return $T.class==obj.getClass();\n", routeType);
        builder.addMethod(methodBuilder.build());
    }

    private void addCallMethod(TypeSpec.Builder builder, String className, Map<String, MethodInfo> map) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(METHOD_CALL).addModifiers(Modifier.PUBLIC);
        methodBuilder.addParameter(String.class, ARG_NAMES[0], Modifier.FINAL).varargs();
        methodBuilder.addParameter(Object[].class, ARG_NAMES[1], Modifier.FINAL).varargs();
        methodBuilder.addAnnotation(Override.class);
        methodBuilder.returns(Object.class);
        StringBuilder javaDoc = new StringBuilder();
        Map<String, String> arrConvertMap = new HashMap<>();
        for (Map.Entry<String, MethodInfo> entry : map.entrySet()) {
            String methodName = entry.getKey();
            MethodInfo methodInfo = entry.getValue();
            javaDoc.append("name: ").append(methodName).append("\n");
            String s = Arrays.toString(methodInfo.getArgType());
            javaDoc.append("call: ").append("{@link ").append(className).append("#").append(methodInfo.getMethodName()).append("(").append(s.substring(1, s.length() - 1)).append(")}\n");
            javaDoc.append("desc: ").append(methodInfo.getDesc()).append("\n");
            methodBuilder.beginControlFlow("if($L.equals($S))", ARG_NAMES[0], methodName);
            StringBuilder argBuilder = new StringBuilder();
            String[] argTypes = methodInfo.getArgType();
            for (int i = 0; i < argTypes.length; i++) {
                String argType = argTypes[i];
                javaDoc.append("argType").append(i).append(": {@link ").append(argType).append("}\n");
                if (argType.contains("...")) {
                    String sign = MD5.encode(s);
                    String convertMethodName = methodName + "_" + sign + "_convert_" + i;
                    String convertType = argType.replace("...", "[]");
                    arrConvertMap.put(convertMethodName, convertType);
                    String varargs = convertMethodName + "(java.util.Arrays.copyOfRange(" + ARG_NAMES[1] + ", " + i + " , " + ARG_NAMES[1] + ".length))";
                    argBuilder.append("(").append(convertType).append(")").append(" ").append(varargs).append(" ,");
                } else {
                    argBuilder.append("(").append(argType).append(")").append(" ").append(ARG_NAMES[1]).append("[").append(i).append("]").append(" ,");
                }
            }
            if (argBuilder.length() > 0) {
                argBuilder.deleteCharAt(argBuilder.length() - 1);
            }
            javaDoc.append("returnType: ").append(methodInfo.getReturnType()).append("\n\n----------------------\n\n");
//            builder.append(PROXY_FIELD_NAME).append(".").append(methodInfo.getMethodName()).append("(").append(argBuilder).append(");\n}");
            if ("void" .equals(methodInfo.getReturnType())) {
                methodBuilder.addStatement("$L.$L($L)", PROXY_FIELD_NAME, methodInfo.getMethodName(), argBuilder.toString());
            } else {
                methodBuilder.addStatement("return $L.$L($L)", PROXY_FIELD_NAME, methodInfo.getMethodName(), argBuilder.toString());
            }
            methodBuilder.endControlFlow();
        }
        methodBuilder.addCode("\nreturn null;\n");
        methodBuilder.addJavadoc(javaDoc.toString());
        builder.addMethod(methodBuilder.build());
        addArrConvertMethod(builder, arrConvertMap);
    }

    private void addArrConvertMethod(TypeSpec.Builder builder, Map<String, String> arrConvertMap) {
        for (Map.Entry<String, String> entry : arrConvertMap.entrySet()) {
            String methodName = entry.getKey();
            String type = entry.getValue();

            TypeName typeName = TypeNameUtils.getTypeName(type.replace("[]", ""));
            ArrayTypeName arrayTypeName = ArrayTypeName.of(typeName);
            System.out.println(arrayTypeName);
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                    .addParameter(Object[].class, "objs")
                    .returns(arrayTypeName)
                    .addStatement("$T arr=new $T[objs.length]", arrayTypeName, typeName);
            methodBuilder.beginControlFlow("for(int i = 0;i<objs.length;i++)")
                    .addStatement("arr[i]=($T)objs[i]", typeName)
                    .endControlFlow();
            methodBuilder.addStatement("return arr");
            builder.addMethod(methodBuilder.build());
        }
    }

}
