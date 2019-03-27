package com.wrbug.componentrouter.sample;

import android.app.Application;

import com.wrbug.componentrouter.annotation.ObjectRoute;
import com.wrbug.componentrouter.annotation.SingletonRouter;


@ObjectRoute(ObjectRoute.APPLICATION_PATH)
public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    @SingletonRouter
    public static App getInstance() {
        return instance;
    }
}
