package com.wrbug.componentrouter;


public class ComponentRouter {
    public static ComponentRouterProxy createProxy(Object obj) {
        if (obj == null) {
            return DefaultComponentRouterProxy.getInstance();
        }
        return ComponentRouterFinder.get(obj);
    }

    public static ComponentRouterInstance build(String path) {
        if (path == null || path.isEmpty()) {
            return DefaultComponentRouterInstance.getGefault();
        }
        return ComponentRouterInstanceFinder.get(path);
    }
}
