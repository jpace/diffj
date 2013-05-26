package org.incava.diffj.type;

import org.incava.analysis.FileDiff;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

public class TestInnerClass extends ItemsTest {
    public TestInnerClass(String name) {
        super(name);
    }

    protected FileDiff makeClassRef(String from, String to, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeRef(from, to, CLASS_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeClassRef(String from, String to, LocationRange fromLoc, LocationRange toLoc) {
        return makeRef(from, to, CLASS_MSGS, fromLoc, toLoc);
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
                 
                 makeClassRef(null, "I2Test", locrg(2, 5, 3, 5), locrg(4, 9, 5, 9)));
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
                 
                 makeClassRef("I2Test", null, locrg(3, 9, 4, 9), locrg(2, 5, 3, 5)));
    }
}
