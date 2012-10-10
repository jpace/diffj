package org.incava.diffj;

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

                 makeConstructorRef(null, "Test(String)", loc(1, 1), loc(3, 1), loc(3, 5), loc(3, 21)));
    }

    public void testClassConstructorRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    public Test(int i, double d, float f) {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeConstructorRef("Test(int, double, float)", null, loc(3, 12), loc(3, 44), loc(1, 1), loc(3, 1)));
    }
}
