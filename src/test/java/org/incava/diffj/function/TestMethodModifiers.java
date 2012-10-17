package org.incava.diffj.function;

import org.incava.diffj.*;

public class TestMethodModifiers extends ItemsTest {
    public TestMethodModifiers(String name) {
        super(name);
    }

    public void testModifierAdded() {
        evaluate(new Lines("class Test {",
                           "    void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    static void foo() {}",
                           "}"),
                 
                 makeModifierRef(null, "static", loc(2, 5), loc(2, 8), loc(3, 5), loc(3, 10)));
    }

    public void testModifierAddedToExisting() {
        evaluate(new Lines("class Test {",
                           "    public void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    static public void foo() {}",
                           "}"),
                 
                 makeModifierRef(null, "static", loc(2, 5), loc(2, 10), loc(3, 5), loc(3, 10)));
    }

    public void testModifierRemoved() {
        evaluate(new Lines("class Test {",
                           "    final void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),
                 
                 makeModifierRef("final", null, loc(2, 5), loc(2, 9), loc(3, 5), loc(3, 8)));
    }
}
