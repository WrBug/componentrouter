package com.wrbug.componentrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通信方法注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface MethodRouter {
    /**
     * 方法 path
     * @return
     */
    String value();

    /**
     * 方法注释
     * @return
     */
    String javaDocDesc() default "";
}
