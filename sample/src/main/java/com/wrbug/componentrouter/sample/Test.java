package com.wrbug.componentrouter.sample;

import android.os.Bundle;
import android.util.Log;

import com.wrbug.componentrouter.MethodRouter;
import com.wrbug.componentrouter.ObjectRoute;

import java.util.Arrays;

@ObjectRoute()
public class Test {
    @MethodRouter("test")
    void test(String aa, int a,String... eeee) {
        Log.i("aaaa", aa);
        Log.i("aaaa", a + "");
        Log.i("aaaa", Arrays.toString(eeee));
    }

    @MethodRouter("test1")
    void test1(String aa, int a, Bundle... eeee) {

    }
}
