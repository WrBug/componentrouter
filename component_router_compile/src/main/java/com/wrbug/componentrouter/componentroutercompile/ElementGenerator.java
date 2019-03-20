package com.wrbug.componentrouter.componentroutercompile;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;

public abstract class ElementGenerator implements Generator {

    private Filer mFiler;
    private TypeElement mElement;

    public ElementGenerator(Filer filer) {
        mFiler = filer;
    }

    public void setElement(TypeElement element) {
        mElement = element;
    }

    public abstract TypeSpec onCreateTypeSpec(TypeElement element, String packageName, String className);

    @Override
    public void generate() {
        String qualifiedName = mElement.getQualifiedName().toString();
        String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
        String className = qualifiedName.substring(packageName.length() + 1);
        try {
            JavaFile javaFile = JavaFile.builder(packageName, onCreateTypeSpec(mElement, packageName, className)).build();
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}