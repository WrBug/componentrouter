package com.wrbug.componentrouter;

public class EmptyPathException extends Exception{
    public EmptyPathException() {
    }

    public EmptyPathException(String s) {
        super(s);
    }
}
