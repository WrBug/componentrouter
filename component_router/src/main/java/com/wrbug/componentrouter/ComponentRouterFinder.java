package com.wrbug.componentrouter;


/**
 * 占坑类，apt生成新文件，仅供编译用
 */
public class ComponentRouterFinder {
    /**
     * 获取instance代理，obj 需要 {@link com.wrbug.componentrouter.annotation.ObjectRoute} 注解
     *
     * @param obj
     * @return  {@link ComponentRouterProxy} 由于文件合并不识别ComponentRouterInstance ，只能用object
     */
    public static Object get(Object obj) {
        return null;
    }
}
