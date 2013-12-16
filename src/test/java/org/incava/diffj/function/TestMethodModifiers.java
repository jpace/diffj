package org.incava.diffj.function;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;

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
                 
                 makeModifierRef(locrg(2, 5, 8), locrg(3, 5, 10), null, "static"));
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
                 
                 makeModifierRef(locrg(2, 5, 10), locrg(3, 5, 10), null, "static"));
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
                 
                 makeModifierRef(locrg(2, 5, 9), locrg(3, 5, 8), "final", null));
    }
}
