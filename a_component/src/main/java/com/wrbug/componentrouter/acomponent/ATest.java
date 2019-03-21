package com.wrbug.componentrouter.acomponent;

import com.wrbug.componentrouter.annotation.ConstructorRouter;
import com.wrbug.componentrouter.annotation.MethodRouter;
import com.wrbug.componentrouter.annotation.ObjectRoute;

@ObjectRoute("/a/ATest")
public class ATest {
    @ConstructorRouter
    public ATest() {
    }

    @ConstructorRouter
    public ATest(String tag,int aaa) {
    }
    @ConstructorRouter
    public ATest(String tag,String aaa) {
    }

    @ConstructorRouter
    public ATest(Object[] tag) {
    }

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
