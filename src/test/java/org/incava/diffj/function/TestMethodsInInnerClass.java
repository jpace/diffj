package org.incava.diffj.function;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;

public class TestMethodsInInnerClass extends ItemsTest {
    public TestMethodsInInnerClass(String name) {
        super(name);
    }

    public void testMethodAdded() {
        evaluate(new Lines("class Test {",
                           "    class ITest {",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    class ITest {",
                           "        void foo() {}",
                           "    }",
                           "}"),
                 
                 makeMethodRef(null, "foo()", locrg(2, 5, 3, 5), locrg(4, 9, 21)));
    }

    public void testMethodRemoved() {
        evaluate(new Lines("class Test {",
                           "    class ITest {",
                           "        void foo() {}",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    class ITest {",
                           "    }",
                           "}"),

                 makeMethodRef("foo()", null, locrg(3, 9, 21), locrg(3, 5, 4, 5)));
    }

    /**
     * Tests a method within an inner class within an inner class.
     * all hail recursion!
     */
    public void testInnerClassMethodAdded() {
        evaluate(new Lines("class Test {",
                           "    class ITest {",
                           "        class I2Test {",
                           "        }",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    class ITest {",
                           "        class I2Test {",
                           "            void foo() {}",
                           "        }",
                           "    }",
                           "}"),
                 
                 makeMethodRef(null, "foo()", locrg(3, 9, 4, 9), locrg(5, 13, 25)));
    }

    public void testInnerClassMethodRemoved() {
        evaluate(new Lines("class Test {",
                           "    class ITest {",
                           "        class I2Test {",
                           "            void foo(  String s) {}",
                           "        }",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    class ITest {",
                           "        class I2Test {",
                           "        }",
                           "    }",
                           "}"),
                 
                 makeMethodRef("foo(String)", null, locrg(4, 13, 35), locrg(4, 9, 5, 9)));
    }
}
