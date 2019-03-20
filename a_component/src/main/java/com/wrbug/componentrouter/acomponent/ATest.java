package com.wrbug.componentrouter.acomponent;

import com.wrbug.componentrouter.MethodRouter;
import com.wrbug.componentrouter.ObjectRoute;

@ObjectRoute()
public class ATest {
    @MethodRouter("test")
    String test() {
        return "ATest";
    }

    @MethodRouter("test2")
    void test(String... aa) {

    }

    @MethodRouter("test3")
    void test(int b, int... aa) {

    }
}
