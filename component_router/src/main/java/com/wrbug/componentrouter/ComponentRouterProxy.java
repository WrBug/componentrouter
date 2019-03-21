package com.wrbug.componentrouter;

import com.wrbug.componentrouter.annotation.MethodRouter;

public interface ComponentRouterProxy {
    /**
     * @param path 远程调用path  {@link MethodRouter#value()}
     * @param args 远程方法参数
     * @param <T>
     * @return
     */
    <T> T call(String path, Object... args);

}
