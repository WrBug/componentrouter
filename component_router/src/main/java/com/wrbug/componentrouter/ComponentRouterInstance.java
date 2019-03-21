package com.wrbug.componentrouter;

public interface ComponentRouterInstance {
    ComponentRouterProxy getProxy();

    <T> T getInstance();
}
