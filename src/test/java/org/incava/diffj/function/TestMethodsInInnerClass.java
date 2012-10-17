package org.incava.diffj.function;

import org.incava.diffj.*;

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
                 
                 makeMethodRef(null, "foo()", loc(2, 5), loc(3, 5), loc(4, 9), loc(4, 21)));
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

                 makeMethodRef("foo()", null, loc(3, 9), loc(3, 21), loc(3, 5), loc(4, 5)));
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
                 
                 makeMethodRef(null, "foo()", loc(3, 9), loc(4, 9), loc(5, 13), loc(5, 25)));
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
                 
                 makeMethodRef("foo(String)", null, loc(4, 13), loc(4, 35), loc(4, 9), loc(5, 9)));
    }

}
