package com.wrbug.componentrouter;
/**
 * 占坑类，apt生成新文件，仅供编译用
 */
public interface ComponentRouterInstance {
    /**
     * 获取代理
     * @return nullable
     */
    ComponentRouterProxy getProxy();

    /**
     * 获取实例
     * @return nullable
     */
    <T> T getInstance();
}
