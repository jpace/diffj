package org.incava.diffj.type;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;

public class TestInnerInterface extends ItemsTest {
    public TestInnerInterface(String name) {
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
                 
                 makeMethodRef(null, "foo()", locrg(2, 5, 3, 5), locrg(4, 9, 19)));
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
                 
                 makeMethodRef("foo()", null, locrg(3, 9, 19), locrg(3, 5, 4, 5)));
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
                 
                 makeInterfaceRef(null, "I2Test", locrg(2, 5, 3, 5), locrg(4, 9, 5, 9)));
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
                 
                 makeInterfaceRef("I2Test", null, locrg(3, 9, 4, 9), locrg(2, 5, 3, 5)));
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
                 
                 makeMethodRef(null, "foo()", locrg(3, 9, 4, 9), locrg(5, 13, 23)));
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
                 
                 makeMethodRef("foo()", null, locrg(4, 13, 23), locrg(4, 9, 5, 9)));
    }
}
