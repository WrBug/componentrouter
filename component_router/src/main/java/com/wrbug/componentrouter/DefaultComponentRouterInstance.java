package com.wrbug.componentrouter;

public class DefaultComponentRouterInstance implements ComponentRouterInstance {
    public volatile static DefaultComponentRouterInstance instance;

    public static synchronized DefaultComponentRouterInstance getGefault() {
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
        return DefaultComponentRouterProxy.getInstance();
    }

    @Override
    public <T> T getInstance() {
        return null;
    }
}
