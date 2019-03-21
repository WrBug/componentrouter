package com.wrbug.componentrouter.componentroutercompile;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

public class TypeNameUtils {
    public static TypeName getTypeName(String fullClassName) {
        if (fullClassName.contains("[]")) {
            TypeName simpleName = getTypeName(fullClassName.replace("[]", ""));
            return ArrayTypeName.of(simpleName);
        }
        int index = fullClassName.lastIndexOf(".");
        if (index == -1) {
            fullClassName = fullClassName.toLowerCase();
            switch (fullClassName) {
                case "short":
                    return TypeName.SHORT;
                case "int":
                    return TypeName.INT;
                case "long":
                    return TypeName.LONG;
                case "float":
                    return TypeName.FLOAT;
                case "double":
                    return TypeName.DOUBLE;
                case "char":
                    return TypeName.CHAR;
                case "byte":
                    return TypeName.BYTE;
                case "boolean":
                    return TypeName.BOOLEAN;
                default:
                    return TypeName.INT;
            }
        }
        String packageName = fullClassName.substring(0, index);
        String className = fullClassName.replace(packageName, "").replace(".", "");
        return ClassName.get(packageName, className);
    }
}
