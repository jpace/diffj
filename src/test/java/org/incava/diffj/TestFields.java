package org.incava.diffj;

public class TestFields extends ItemsTest {
    public TestFields(String name) {
        super(name);
    }

    public void testClassOneFieldAdded() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int i;",
                           "}"),
                 
                 makeFieldRef(null, "i", loc(1, 1), loc(3, 1), loc(3, 5), loc(3, 10)));
    }

    public void testClassOneFieldRemoved() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeFieldRef("i", null, loc(2, 5), loc(2, 10), loc(1, 1), loc(3, 1)));
    }

    public void testClassOneFieldRemovedOneFieldAdded() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    String j;",
                           "",
                           "}"),
                 
                 makeFieldRef(null, "j",  loc(1, 1), loc(4,  1), loc(2, 5), loc(2, 13)),
                 makeFieldRef("i",  null, loc(2, 5), loc(2, 10), loc(1, 1), loc(4,  1)));
    }
}
