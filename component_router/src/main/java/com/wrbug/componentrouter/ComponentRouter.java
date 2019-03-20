package com.wrbug.componentrouter;


public class ComponentRouter {
    public static ComponentRouterProxy createProxy(Object obj) {
        if (obj == null) {
            return null;
        }
        return ComponentRouterFinder.get(obj);
    }
}
