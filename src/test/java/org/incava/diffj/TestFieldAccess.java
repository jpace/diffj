package org.incava.diffj;

public class TestFieldAccess extends ItemsTest {
    public TestFieldAccess(String name) {
        super(name);
    }

    public void testAccessAdded() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    private int i;",
                           "}"),
                 
                 makeAccessRef(null, "private", loc(2, 5), loc(2, 7), loc(3, 5), loc(3, 11)));
    }

    public void testAccessRemoved() {
        evaluate(new Lines("class Test {",
                           "    public int i;",
                           "",
                           "}"),
                 
                 new Lines("class Test {",
                           "",
                           "    int i;",
                           "}"),
                 
                 makeAccessRef("public", null, loc(2, 5), loc(2, 10), loc(3, 5), loc(3, 7)));
    }

    public void testAccessChanged() {
        evaluate(new Lines("class Test {",
                           "    private int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public int i;",
                           "}"),
                 
                 makeAccessRef("private", "public", loc(2, 5), loc(3, 5)));
    }
}
