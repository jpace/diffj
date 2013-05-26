package org.incava.diffj.function;

import org.incava.analysis.FileDiff;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.ijdk.text.LocationRange;

public class TestCtors extends ItemsTest {
    public TestCtors(String name) {
        super(name);
    }

    protected FileDiff makeConstructorRef(String from, String to, LocationRange fromLoc, LocationRange toLoc) {
        return makeRef(from, to, CONSTRUCTOR_MSGS, fromLoc, toLoc);
    }

    public void testClassConstructorAdded() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(String s) {}",
                           "}"),

                 makeConstructorRef(null, "Test(String)", locrg(1, 1, 3, 1), locrg(3, 5, 21)));
    }

    public void testClassConstructorRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    public Test(int i, double d, float f) {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeConstructorRef("Test(int, double, float)", null, locrg(3, 12, 44), locrg(1, 1, 3, 1)));
    }
}
