package org.incava.diffj;

public class TestMethodCodeChange extends ItemsTest {
    public TestMethodCodeChange(String name) {
        super(name);
    }

    public void xtestCodeNotChanged() {
        evaluate(new Lines("class Test {",
                           "    int bar() { return -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int bar() { ",
                           "        return -1;",
                           "    }",
                           "",
                           "}"),

                 NO_CHANGES);
    }

    public void testCodeChangedInsert() {
        getOutput(new Lines("class Test {",
                            "    int bar() { ",
                            "        if (true)",
                            "            foo();",
                            "    }",
                            "}"),

                  new Lines("class Test {",
                            "    int bar() { ",
                            "        if (true) {",
                            "            foo();",
                            "        }",
                            "    }",
                            "}"));
        // evaluate(new Lines("class Test {",
        //                    "    int bar() { return -1; }",
        //                    "",
        //                    "}"),

        //          new Lines("class Test {",
        //                    "    int bar() { return (-1); }",
        //                    "}"),
                 
        //          makeCodeChangedRef(MethodDiff.CODE_CHANGED, "bar()", loc(2, 25), loc(2, 25), loc(4, 17), loc(4, 17)));
    }

    public boolean showContext() {
        return true;
    }
}
