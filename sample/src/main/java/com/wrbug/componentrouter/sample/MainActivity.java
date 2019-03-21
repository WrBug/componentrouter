package com.wrbug.componentrouter.sample;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wrbug.componentrouter.ComponentRouter;
import com.wrbug.componentrouter.ComponentRouterInstance;
import com.wrbug.componentrouter.ComponentRouterProxy;
import com.wrbug.componentrouter.annotation.MethodRouter;
import com.wrbug.componentrouter.annotation.ObjectRoute;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ComponentRouterInstance build;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        build = ComponentRouter.build("/a/AFragment");
        Fragment fragment = build.getInstance();
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss();
        }
    }

    public void onClick(View view) {
        String text = build.getProxy().call("getText");
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
