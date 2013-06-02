package org.incava.diffj.type;

import org.incava.analysis.FileDiff;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.ijdk.text.LocationRange;

public class TestInnerClass extends ItemsTest {
    public TestInnerClass(String name) {
        super(name);
    }

    protected FileDiff makeClassRef(LocationRange fromLoc, LocationRange toLoc, String from, String to) {
        return makeRef(fromLoc, toLoc, CLASS_MSGS, from, to);
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
                 
                 makeClassRef(locrg(2, 5, 3, 5), locrg(4, 9, 5, 9), null, "I2Test"));
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
                 
                 makeClassRef(locrg(3, 9, 4, 9), locrg(2, 5, 3, 5), "I2Test", null));
    }
}
