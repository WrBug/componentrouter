package com.wrbug.componentrouter;

import com.wrbug.componentrouter.annotation.ObjectRoute;

/**
 * 占坑类，apt生成新文件，仅供编译用
 */
public class ComponentRouterInstanceFinder {
    /**
     * @param path  {@link ObjectRoute#value()}
     * @param parameters    constructorParams
     * @return  {@link ComponentRouterInstance}  由于文件合并不识别ComponentRouterInstance ，只能用object
     */
    public static Object get(String path, Object... parameters) {
        return null;
    }
}
