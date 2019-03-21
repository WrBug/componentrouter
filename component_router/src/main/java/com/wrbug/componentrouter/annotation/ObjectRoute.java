package com.wrbug.componentrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ObjectRoute {
    /**
     * 类 path，不得为空
     * <p>
     * ComponentRouter.build(path)
     *
     * @return
     */
    String value();
}
