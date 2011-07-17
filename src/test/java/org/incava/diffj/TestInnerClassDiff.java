package org.incava.diffj;

import java.awt.Point;
import java.text.MessageFormat;
import org.incava.analysis.FileDiff;


public class TestInnerClassDiff extends AbstractTestItemDiff {

    protected final static String[] METHOD_MSGS = new String[] {
        TypeDiff.METHOD_REMOVED,
        TypeDiff.METHOD_CHANGED, 
        TypeDiff.METHOD_ADDED,
    };

    protected final static String[] CLASS_MSGS = new String[] {
        TypeDiff.INNER_CLASS_REMOVED,
        null,
        TypeDiff.INNER_CLASS_ADDED,
    };
    
    public TestInnerClassDiff(String name) {
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
                 
                 makeRef(null, "foo()", METHOD_MSGS, loc(2, 5), loc(3, 5), loc(4, 9), loc(4, 21)));
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

                 makeRef("foo()", null, METHOD_MSGS, loc(3, 9), loc(3, 21), loc(3, 5), loc(4, 5)));
    }

    public void testInnerClassAdded() {
        evaluate(new Lines("class Test {",
                           "    class ITest {",
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
                 
                 makeRef(null, "I2Test", CLASS_MSGS, loc(2, 5), loc(3, 5), loc(4, 9), loc(5, 9)));
    }

    public void testInnerClassRemoved() {
        evaluate(new Lines("class Test {",
                           "    class ITest {",
                           "        class I2Test {",
                           "        }",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    class ITest {",
                           "    }",
                           "}"),
                 
                 makeRef("I2Test", null, CLASS_MSGS, loc(3, 9), loc(4, 9), loc(2, 5), loc(3, 5)));
    }

    public void testInnerClassMethodAdded() {
        // all hail recursion!
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
                 
                 makeRef(null, "foo()", METHOD_MSGS, loc(3, 9), loc(4, 9), loc(5, 13), loc(5, 25)));
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
                 
                 makeRef("foo(String)", null, METHOD_MSGS, loc(4, 13), loc(4, 35), loc(4, 9), loc(5, 9)));
    }

}
