package org.incava.diffj;

public class TestTypes extends ItemTest {
    public TestTypes(String name) {
        super(name);
    }

    public void testAllTypesAdded() {
        evaluate(new Lines(""),

                 new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(null, "Test",  loc(1, 1), loc(1, 1), loc(1, 1), loc(2, 1)),
                 makeTypeRef(null, "Test2", loc(1, 1), loc(1, 1), loc(3, 1), loc(4, 1)));

        evaluate(new Lines("import foo.Bar;"),

                 new Lines("import foo.Bar;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(null, "Test",  loc(1, 1), loc(1, 16), loc(2, 1), loc(3, 1)),
                 makeTypeRef(null, "Test2", loc(1, 1), loc(1, 16), loc(4, 1), loc(5, 1)));

        evaluate(new Lines("package foo;",
                           ""),

                 new Lines("package foo;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(null, "Test",  loc(1, 1), loc(2, 1), loc(2, 1), loc(3, 1)),
                 makeTypeRef(null, "Test2", loc(1, 1), loc(2, 1), loc(4, 1), loc(5, 1)));
    }

    public void testAllTypesRemoved() {
        evaluate(new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),

                 new Lines(""),
                 
                 makeTypeRef("Test",  null, loc(1, 1), loc(2, 1), loc(1, 1), loc(1, 1)),
                 makeTypeRef("Test2", null, loc(3, 1), loc(4, 1), loc(1, 1), loc(1, 1)));
    }
}
