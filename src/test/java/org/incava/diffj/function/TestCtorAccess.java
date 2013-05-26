package org.incava.diffj.function;

import org.incava.diffj.*;

public class TestCtorAccess extends ItemsTest {
    public TestCtorAccess(String name) {
        super(name);
    }

    public void testAccessAdded() {
        evaluate(new Lines("class Test {",
                           "    Test() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public Test() {}",
                           "}"),
                 
                 makeAccessRef(null, "public", locrg(2, 5, 8), locrg(3, 5, 10)));
    }

    public void testAccessRemoved() {
        evaluate(new Lines("class Test {",
                           "    public Test() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() {}",
                           "}"),
                 
                 makeAccessRef("public", null, locrg(2, 5, 10), locrg(3, 5, 8)));
    }

    public void testAccessChanged() {
        evaluate(new Lines("class Test {",
                           "    private Test() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public Test() {}",
                           "}"),
                 
                 makeAccessRef("private", "public", loc(2, 5), loc(3, 5)));
    }
}
