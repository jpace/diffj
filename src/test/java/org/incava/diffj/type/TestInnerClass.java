package org.incava.diffj.type;

import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;

public class TestInnerClass extends ItemsTest {
    public TestInnerClass(String name) {
        super(name);
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
                 
                 new FileDiffAdd(locrg(2, 5, 3, 5), locrg(4, 9, 5, 9), Type.INNER_CLASS_ADDED, "I2Test"));
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
                 
                 new FileDiffDelete(locrg(3, 9, 4, 9), locrg(2, 5, 3, 5), Type.INNER_CLASS_REMOVED, "I2Test"));
    }
}
