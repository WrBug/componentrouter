package com.wrbug.componentrouter.componentroutercompile.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wrbug.componentrouter.ComponentRouter;
import com.wrbug.componentrouter.ComponentRouterInstance;
import com.wrbug.componentrouter.ComponentRouterProxy;
import com.wrbug.componentrouter.DefaultComponentRouterInstance;
import com.wrbug.componentrouter.DefaultComponentRouterProxy;
import com.wrbug.componentrouter.annotation.ObjectRoute;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.wrbug.componentrouter.componentroutercompile.Constant.*;

/**
 */
public class ComponentInstanceRouterFinderGenerator implements Generator {
    private Filer mFiler;
    private Set<? extends Element> mElements;
    private static final String singletonCacheName = "singletonCache";

    public ComponentInstanceRouterFinderGenerator(Filer filer, Set<? extends Element> elements) {
        mFiler = filer;
        mElements = elements;
    }

    @Override
    public void generate() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(INSTANCE_FINDER_CLASS_NAME)
                .addField(FieldSpec.builder(ParameterizedTypeName.get(Map.class, String.class, Object.class), singletonCacheName, Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC).initializer("new $T()", HashMap.class).build())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        MethodSpec.Builder getMethodBuilder = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(Object.class)
                .addParameter(String.class, "path")
                .addParameter(Object[].class, "parameters").varargs();
        getMethodBuilder.beginControlFlow("if($L.containsKey(path))", singletonCacheName)
                .addStatement("return $L.get(path)", singletonCacheName)
                .endControlFlow();
        for (Element element : mElements) {
            if (element instanceof TypeElement) {
                ObjectRoute route = element.getAnnotation(ObjectRoute.class);
                if (route == null) {
                    continue;
                }
                String path = route.value();
                if (path.isEmpty()) {
                    continue;
                }
                TypeElement typeElement = (TypeElement) element;
                final String name = typeElement.getQualifiedName().toString() + INSTANCE_PROXY_SUFFIX;
                final String packageName = name.substring(0, name.lastIndexOf("."));
                final String className = name.substring(packageName.length() + 1);
                final ClassName classType = ClassName.get(packageName, className);
                getMethodBuilder.beginControlFlow("if ($S.equals(path))", path)
                        .addStatement("$T obj= new $T(parameters)", classType, classType)
                        .beginControlFlow("if($T.isSingleton)", classType)
                        .addStatement("$L.put(path,obj)", singletonCacheName)
                        .endControlFlow()
                        .addStatement("return obj")
                        .endControlFlow();
            }
        }
        getMethodBuilder.addStatement("return null");
        builder.addMethod(getMethodBuilder.build());
        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, builder.build()).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
        }
    }
}
