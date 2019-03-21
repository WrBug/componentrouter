package com.wrbug.componentrouter.componentroutercompile.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.wrbug.componentrouter.EmptyPathException;
import com.wrbug.componentrouter.componentroutercompile.util.Log;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;

public abstract class ElementGenerator implements Generator {

    private Filer mFiler;
    private Log mLog;
    private TypeElement mElement;

    public ElementGenerator(Filer filer, Log log) {
        mFiler = filer;
        mLog = log;
    }

    public void setElement(TypeElement element) {
        mElement = element;
    }

    void log(String msg) {
        mLog.printMessage(this.getClass().getSimpleName() + " : " + msg);
    }

    void log(String tag, String msg) {
        mLog.printMessage(this.getClass().getSimpleName() + " : " + tag + " : " + msg);
    }

    public abstract TypeSpec onCreateTypeSpec(TypeElement element, String packageName, String className) throws Exception;

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