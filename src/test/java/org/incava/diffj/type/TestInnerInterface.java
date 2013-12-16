package org.incava.diffj.type;

import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.function.Method;
import org.incava.diffj.util.Lines;

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
                 
                 new FileDiffAdd(locrg(2, 5, 3, 5), locrg(4, 9, 19), Method.METHOD_ADDED, "foo()"));
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
                 
                 new FileDiffDelete(locrg(3, 9, 19), locrg(3, 5, 4, 5), Method.METHOD_REMOVED, "foo()"));
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
                 
                 new FileDiffAdd(locrg(2, 5, 3, 5), locrg(4, 9, 5, 9), Type.INNER_INTERFACE_ADDED, "I2Test"));
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
                 
                 new FileDiffDelete(locrg(3, 9, 4, 9), locrg(2, 5, 3, 5), Type.INNER_INTERFACE_REMOVED, "I2Test"));
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
                 
                 new FileDiffAdd(locrg(3, 9, 4, 9), locrg(5, 13, 23), Method.METHOD_ADDED, "foo()"));
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
                 
                 new FileDiffDelete(locrg(4, 13, 23), locrg(4, 9, 5, 9), Method.METHOD_REMOVED, "foo()"));
    }
}
