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
                 
                 makeMethodAddedRef(locrg(2, 5, 3, 5), locrg(4, 9, 19), "foo()"));
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
                 
                 makeMethodRemovedRef(locrg(3, 9, 19), locrg(3, 5, 4, 5), "foo()"));
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
                 
                 makeInterfaceAddedRef(locrg(2, 5, 3, 5), locrg(4, 9, 5, 9), "I2Test"));
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
                 
                 makeInterfaceRemovedRef(locrg(3, 9, 4, 9), locrg(2, 5, 3, 5), "I2Test"));
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
                 
                 makeMethodAddedRef(locrg(3, 9, 4, 9), locrg(5, 13, 23), "foo()"));
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
                 
                 makeMethodRemovedRef(locrg(4, 13, 23), locrg(4, 9, 5, 9), "foo()"));
    }
}
