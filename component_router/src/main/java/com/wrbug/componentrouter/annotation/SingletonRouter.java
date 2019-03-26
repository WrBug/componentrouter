package com.wrbug.componentrouter.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 单例注解，使用此注解 ConstructorRouter 注解将失效
 * 用于获取单例的静态方法上
 * exp:
 * <p>
 * public class Test{
 * private static Test instance;
 *
 * @SingletonRouter public static Test getInstance(){
 * if(instance==null){
 * instance=new Test();
 * }
 * return instance
 * }
 * <p>
 * }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface SingletonRouter {
}
