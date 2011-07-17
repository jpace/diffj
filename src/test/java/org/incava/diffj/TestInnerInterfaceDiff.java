package org.incava.diffj;

import java.awt.Point;
import java.text.MessageFormat;


public class TestInnerInterfaceDiff extends AbstractTestItemDiff {

    protected final static String[] METHOD_MSGS = new String[] {
        TypeDiff.METHOD_REMOVED,
        TypeDiff.METHOD_CHANGED, 
        TypeDiff.METHOD_ADDED,
    };

    protected final static String[] INTERFACE_MSGS = new String[] {
        TypeDiff.INNER_INTERFACE_REMOVED,
        null,
        TypeDiff.INNER_INTERFACE_ADDED,
    };
    
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
                 
                 makeRef(null, "foo()", METHOD_MSGS, loc(2, 5), loc(3, 5), loc(4, 9), loc(4, 19)));
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
                 
                 makeRef("foo()", null, METHOD_MSGS, loc(3, 9), loc(3, 19), loc(3, 5), loc(4, 5)));
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
                 
                 makeRef(null, "I2Test", INTERFACE_MSGS, loc(2, 5), loc(3, 5), loc(4, 9), loc(5, 9)));
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
                 
                 makeRef("I2Test", null, INTERFACE_MSGS, loc(3, 9), loc(4, 9), loc(2, 5), loc(3, 5)));
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
                 
                 makeRef(null, "foo()", METHOD_MSGS, loc(3, 9), loc(4, 9), loc(5, 13), loc(5, 23)));
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
                 
                 makeRef("foo()", null, METHOD_MSGS, loc(4, 13), loc(4, 23), loc(4, 9), loc(5, 9)));
    }

}
