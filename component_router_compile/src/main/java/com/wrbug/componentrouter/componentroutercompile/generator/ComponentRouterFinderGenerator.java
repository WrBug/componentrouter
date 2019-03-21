package com.wrbug.componentrouter.componentroutercompile.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wrbug.componentrouter.ComponentRouterProxy;
import com.wrbug.componentrouter.DefaultComponentRouterInstance;
import com.wrbug.componentrouter.DefaultComponentRouterProxy;

import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.wrbug.componentrouter.componentroutercompile.Constant.*;

public class ComponentRouterFinderGenerator implements Generator {
    private Filer mFiler;
    private Set<? extends Element> mElements;

    public ComponentRouterFinderGenerator(Filer filer, Set<? extends Element> elements) {
        mFiler = filer;
        mElements = elements;
    }

    @Override
    public void generate() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(FINDER_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        MethodSpec.Builder getMethodBuilder = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addAnnotation(ClassName.get("android.support.annotation", "Nullable"))
                .returns(ComponentRouterProxy.class)
                .addParameter(Object.class, "obj");

        for (Element element : mElements) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                final String name = typeElement.getQualifiedName().toString() + PROXY_SUFFIX;
                final String packageName = name.substring(0, name.lastIndexOf("."));
                final String className = name.substring(packageName.length() + 1);
                final ClassName classType = ClassName.get(packageName, className);
                getMethodBuilder.beginControlFlow("if ($T.is(obj))", classType)
                        .addCode("return new $T(($L)obj);", classType, typeElement.getQualifiedName().toString())
                        .endControlFlow();
            }
        }
        getMethodBuilder.addStatement("return $T.getDefault()", DefaultComponentRouterProxy.class);
        builder.addMethod(getMethodBuilder.build());
        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, builder.build()).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
        }
    }
}
