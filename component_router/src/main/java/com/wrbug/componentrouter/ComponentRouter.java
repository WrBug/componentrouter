package com.wrbug.componentrouter;

import com.wrbug.componentrouter.annotation.*;

public class ComponentRouter {
    /**
     * @param obj {@link ObjectRoute } instance
     * @return
     */
    public static ComponentRouterProxy createProxy(Object obj) {
        if (obj == null) {
            return DefaultComponentRouterProxy.getDefault();
        }
        ComponentRouterProxy componentRouterProxy = (ComponentRouterProxy) ComponentRouterFinder.get(obj);
        if (componentRouterProxy == null) {
            componentRouterProxy = DefaultComponentRouterProxy.getDefault();
        }
        return componentRouterProxy;
    }

    /**
     * @param path       {@link ObjectRoute } path
     * @param parameters {@link ConstructorRouter} or {@link SingletonRouter} parameters
     * @return
     */
    public static ComponentRouterInstance build(String path, Object... parameters) {
        if (path == null || path.isEmpty()) {
            return DefaultComponentRouterInstance.getDefault();
        }
        ComponentRouterInstance componentRouterInstance = (ComponentRouterInstance) ComponentRouterInstanceFinder.get(path, parameters);
        if (componentRouterInstance == null) {
            componentRouterInstance = DefaultComponentRouterInstance.getDefault();
        }
        return componentRouterInstance;
    }
}
