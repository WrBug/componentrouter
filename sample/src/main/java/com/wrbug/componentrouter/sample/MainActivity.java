package com.wrbug.componentrouter.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wrbug.componentrouter.ComponentRouter;
import com.wrbug.componentrouter.ComponentRouterInstance;
import com.wrbug.componentrouter.ComponentRouterProxy;

public class MainActivity extends AppCompatActivity {
    ComponentRouterInstance aFragmentProxy;
    ComponentRouterInstance bFragmentProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aFragmentProxy = ComponentRouter.build("/a/AFragment");
        Fragment fragment = aFragmentProxy.getInstance();
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss();
        }
        bFragmentProxy = ComponentRouter.build("/b/bfragment");
        fragment = bFragmentProxy.getInstance();
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss();
        }

    }


    public void onGetTextClick(View view) {
        String text = aFragmentProxy.getProxy().call("getText");
        bFragmentProxy.getProxy().call("setText", text);
    }

    public void onSaveUserClick(View view) {
        ComponentRouterInstance build = ComponentRouter.build("/a/userManager", this.getApplicationContext());
        build.getProxy().call("saveUsername", ((EditText) findViewById(R.id.usernameEt)).getText().toString());
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }

    public void onGetUserClick(View view) {
        ComponentRouterInstance build = ComponentRouter.build("/a/userManager", this.getApplicationContext());
        String username = build.getProxy().call("getUsername");
        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
    }
}
