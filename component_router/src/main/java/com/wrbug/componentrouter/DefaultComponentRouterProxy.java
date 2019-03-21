package com.wrbug.componentrouter;

public class DefaultComponentRouterProxy implements ComponentRouterProxy {
    public volatile static DefaultComponentRouterProxy instance;

    public static DefaultComponentRouterProxy getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (DefaultComponentRouterProxy.class) {
            if (instance == null) {
                instance = new DefaultComponentRouterProxy();
            }
        }
        return instance;
    }

    @Override
    public <T> T call(String path, Object... args) {
        return null;
    }
}
