package org.incava.diffj.function;

import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;

public class TestCtors extends ItemsTest {
    public TestCtors(String name) {
        super(name);
    }

    public void testClassConstructorAdded() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(String s) {}",
                           "}"),

                 new FileDiffAdd(locrg(1, 1, 3, 1), locrg(3, 5, 21), Ctor.CONSTRUCTOR_ADDED, "Test(String)"));
    }

    public void testClassConstructorRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    public Test(int i, double d, float f) {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 new FileDiffDelete(locrg(3, 12, 44), locrg(1, 1, 3, 1), Ctor.CONSTRUCTOR_REMOVED, "Test(int, double, float)"));
    }
}
