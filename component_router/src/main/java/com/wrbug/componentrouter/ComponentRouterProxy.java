package com.wrbug.componentrouter;

public interface ComponentRouterProxy {
    <T> T call(String path, Object... args);

}
