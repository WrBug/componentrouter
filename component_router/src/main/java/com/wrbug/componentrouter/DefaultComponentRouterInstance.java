package com.wrbug.componentrouter;

public class DefaultComponentRouterInstance implements ComponentRouterInstance {
    public volatile static DefaultComponentRouterInstance instance;

    public static synchronized DefaultComponentRouterInstance getDefault() {
        if (instance != null) {
            return instance;
        }
        synchronized (DefaultComponentRouterInstance.class) {
            if (instance == null) {
                instance = new DefaultComponentRouterInstance();
            }
        }
        return instance;
    }

    @Override
    public ComponentRouterProxy getProxy() {
        return DefaultComponentRouterProxy.getDefault();
    }

    @Override
    public <T> T getInstance() {
        return null;
    }
}
