package com.wrbug.componentrouter;

import java.util.Map;

public class ParameterInfo {
    private Class parameterType;
    private Class previousParameterInfo;
    Map<Class, ParameterInfo> nextParameterInfo;

    public ParameterInfo() {
    }

    public Class getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class parameterType) {
        this.parameterType = parameterType;
    }

    public Map<Class, ParameterInfo> getNextParameterInfo() {
        return nextParameterInfo;
    }

    public void setNextParameterInfo(Map<Class, ParameterInfo> nextParameterInfo) {
        this.nextParameterInfo = nextParameterInfo;
    }

    public Class getPreviousParameterInfo() {
        return previousParameterInfo;
    }
}
