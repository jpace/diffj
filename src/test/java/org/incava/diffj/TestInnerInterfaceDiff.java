package org.incava.diffj;

public class TestInnerInterfaceDiff extends AbstractTestItemDiff {
    public TestInnerInterfaceDiff(String name) {
        super(name);
    }

    public void testMethodAdded() {
        evaluate(new Lines("class Test {",
                           "    interface ITest {",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    interface ITest {",
                           "        void foo();",
                           "    }",
                           "}"),
                 
                 makeMethodRef(null, "foo()", loc(2, 5), loc(3, 5), loc(4, 9), loc(4, 19)));
    }

    public void testMethodRemoved() {
        evaluate(new Lines("class Test {",
                           "    interface ITest {",
                           "        void foo();",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    interface ITest {",
                           "    }",
                           "}"),
                 
                 makeMethodRef("foo()", null, loc(3, 9), loc(3, 19), loc(3, 5), loc(4, 5)));
    }

    public void testInnerInterfaceAdded() {
        evaluate(new Lines("class Test {",
                           "    interface ITest {",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    interface ITest {",
                           "        interface I2Test {",
                           "        }",
                           "    }",
                           "}"),
                 
                 makeInterfaceRef(null, "I2Test", loc(2, 5), loc(3, 5), loc(4, 9), loc(5, 9)));
    }

    public void testInnerInterfaceRemoved() {
        evaluate(new Lines("class Test {",
                           "    interface ITest {",
                           "        interface I2Test {",
                           "        }",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    interface ITest {",
                           "    }",
                           "}"),
                 
                 makeInterfaceRef("I2Test", null, loc(3, 9), loc(4, 9), loc(2, 5), loc(3, 5)));
    }

    public void testInnerInterfaceMethodAdded() {
        // all hail recursion!
        evaluate(new Lines("class Test {",
                           "    interface ITest {",
                           "        interface I2Test {",
                           "        }",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    interface ITest {",
                           "        interface I2Test {",
                           "            void foo();",
                           "        }",
                           "    }",
                           "}"),
                 
                 makeMethodRef(null, "foo()", loc(3, 9), loc(4, 9), loc(5, 13), loc(5, 23)));
    }

    public void testInnerInterfaceMethodRemoved() {
        evaluate(new Lines("class Test {",
                           "    interface ITest {",
                           "        interface I2Test {",
                           "            void foo();",
                           "        }",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    interface ITest {",
                           "        interface I2Test {",
                           "        }",
                           "    }",
                           "}"),
                 
                 makeMethodRef("foo()", null, loc(4, 13), loc(4, 23), loc(4, 9), loc(5, 9)));
    }

}
