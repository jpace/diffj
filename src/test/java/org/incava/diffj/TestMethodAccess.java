package org.incava.diffj;

public class TestMethodAccess extends ItemsTest {
    public TestMethodAccess(String name) {
        super(name);
    }

    public void testAccessAdded() {
        evaluate(new Lines("class Test {",
                           "    void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public void foo() {}",
                           "}"),
                 
                 makeAccessRef(null, "public", loc(2, 5), loc(2, 8), loc(3, 5), loc(3, 10)));
    }

    public void testAccessRemoved() {
        evaluate(new Lines("class Test {",
                           "    public void foo() {}",
                           "",
                           "}"),
                 
                 new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),

                 makeAccessRef("public", null, loc(2, 5), loc(2, 10), loc(3, 5), loc(3, 8)));
    }

    public void testAccessChanged() {
        evaluate(new Lines("class Test {",
                           "    private void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public void foo() {}",
                           "}"),
                 
                 makeAccessRef("private", "public", loc(2, 5), loc(3, 5)));
    }
}
