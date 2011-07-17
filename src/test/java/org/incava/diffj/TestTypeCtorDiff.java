package org.incava.diffj;

import java.awt.Point;
import java.text.MessageFormat;
import org.incava.analysis.FileDiff;


public class TestTypeCtorDiff extends AbstractTestItemDiff {

    protected final static String[] CONSTRUCTOR_MSGS = TestTypeDiff.CONSTRUCTOR_MSGS;

    public TestTypeCtorDiff(String name) {
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

                 makeRef(null, "Test(String)", CONSTRUCTOR_MSGS, loc(1, 1), loc(3, 1), loc(3, 5), loc(3, 21)));
    }

    public void testClassConstructorRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    public Test(int i, double d, float f) {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeRef("Test(int, double, float)", null, CONSTRUCTOR_MSGS, loc(3, 12), loc(3, 44), loc(1, 1), loc(3, 1)));
    }
}
