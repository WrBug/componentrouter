package com.wrbug.componentrouter.sample;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wrbug.componentrouter.ComponentRouter;
import com.wrbug.componentrouter.ComponentRouterProxy;
import com.wrbug.componentrouter.MethodRouter;
import com.wrbug.componentrouter.ObjectRoute;

import java.util.List;

@ObjectRoute("sadfasd")
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @MethodRouter(value = "show", javaDocDesc = "显示toast")
    public void show(String aaa, int a, Object aa, Bundle bundle, Object[] ee, List<MainActivity> list,String... str) {

    }

    public void onClick(View view) {
        Test test = new Test();
        ComponentRouterProxy proxy = ComponentRouter.createProxy(test);
        proxy.call("test", "hahaha",123,"a","b");
    }

    @MethodRouter("show1")
    public List<String> show1(String aa, Runnable runnable) {
        return null;
    }
}
