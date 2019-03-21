package com.wrbug.componentrouter.componentroutercompile.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wrbug.componentrouter.ComponentRouter;
import com.wrbug.componentrouter.ComponentRouterInstance;
import com.wrbug.componentrouter.ComponentRouterProxy;
import com.wrbug.componentrouter.DefaultComponentRouterInstance;
import com.wrbug.componentrouter.DefaultComponentRouterProxy;
import com.wrbug.componentrouter.annotation.ObjectRoute;

import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.wrbug.componentrouter.componentroutercompile.Constant.*;

public class ComponentInstanceRouterFinderGenerator implements Generator {
    private Filer mFiler;
    private Set<? extends Element> mElements;

    public ComponentInstanceRouterFinderGenerator(Filer filer, Set<? extends Element> elements) {
        mFiler = filer;
        mElements = elements;
    }

    @Override
    public void generate() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(INSTANCE_FINDER_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        MethodSpec.Builder getMethodBuilder = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(ComponentRouterInstance.class)
                .addParameter(String.class, "path")
                .addParameter(Object[].class, "parameters").varargs();

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
                        .addCode("return new $T(parameters);", classType)
                        .endControlFlow();
            }
        }
        getMethodBuilder.addStatement("return $T.getDefault()", DefaultComponentRouterInstance.class);
        builder.addMethod(getMethodBuilder.build());
        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, builder.build()).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
        }
    }
}
