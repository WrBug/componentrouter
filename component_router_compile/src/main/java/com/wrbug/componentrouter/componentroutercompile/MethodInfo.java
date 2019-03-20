package com.wrbug.componentrouter.componentroutercompile;

import java.util.Arrays;

public class MethodInfo {
    private String returnType;
    private String methodName;
    private String desc;
    private String[] argType;

    public MethodInfo() {
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getArgType() {
        return argType;
    }

    public void setArgType(String[] argType) {
        this.argType = argType;
    }


    @Override
    public String toString() {
        return "returnType=" + returnType + "\nmethodName=" + methodName + "\nargType=" + Arrays.toString(argType);
    }
}
