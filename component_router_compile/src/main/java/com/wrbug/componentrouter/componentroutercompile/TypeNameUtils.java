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
    public static String getDefaultValue(String fullClassName) {
        TypeName typeName = getTypeName(fullClassName);
        if (typeName == TypeName.SHORT || typeName == TypeName.INT || typeName == TypeName.LONG || typeName == TypeName.FLOAT || typeName == TypeName.DOUBLE || typeName == TypeName.CHAR || typeName == TypeName.BYTE) {
            return String.format("(%s)0", typeName.toString());
        }
        if (typeName == TypeName.BOOLEAN) {
            return "false";
        }
        return String.format("(%s)null", typeName.toString());
    }
}
