package com.wrbug.componentrouter;


public class ComponentRouter {
    public static ComponentRouterProxy createProxy(Object obj) {
        if (obj == null) {
            return DefaultComponentRouterProxy.getDefault();
        }
        return ComponentRouterFinder.get(obj);
    }

    public static ComponentRouterInstance build(String path,Object... parameters) {
        if (path == null || path.isEmpty()) {
            return DefaultComponentRouterInstance.getDefault();
        }
        return ComponentRouterInstanceFinder.get(path,parameters);
    }
}
