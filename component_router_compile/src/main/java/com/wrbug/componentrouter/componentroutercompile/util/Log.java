package com.wrbug.componentrouter.componentroutercompile.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class Log {
    private Messager mMessager;

    public Log(Messager mMessager) {
        this.mMessager = mMessager;
    }

    public void printMessage(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
