package org.incava.diffj.type;

import org.incava.analysis.FileDiff;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.ijdk.text.LocationRange;

public class TestInnerClass extends ItemsTest {
    public TestInnerClass(String name) {
        super(name);
    }

    protected FileDiff makeClassAddedRef(LocationRange fromLoc, LocationRange toLoc, String added) {
        return makeRef(fromLoc, toLoc, CLASS_MSGS, null, added);
    }

    protected FileDiff makeClassRemovedRef(LocationRange fromLoc, LocationRange toLoc, String removed) {
        return makeRef(fromLoc, toLoc, CLASS_MSGS, removed, null);
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
                 
                 makeClassAddedRef(locrg(2, 5, 3, 5), locrg(4, 9, 5, 9), "I2Test"));
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
                 
                 makeClassRemovedRef(locrg(3, 9, 4, 9), locrg(2, 5, 3, 5), "I2Test"));
    }
}
