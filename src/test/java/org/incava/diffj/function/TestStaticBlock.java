package org.incava.diffj.function;

import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;

public class TestStaticBlock extends ItemsTest {
    public TestStaticBlock(String name) {
        super(name);
    }

    public void testStaticBlockRemoved() {
        // evaluate(new Lines("class Test {",
        //                    "    { int i; }",
        //                    "    void foo() { }",
        //                    "}"),

        //          new Lines("class Test {",
        //                    "}"),
                 
        //          new FileDiffChange(null, locrg(1, 1, 2, 1), locrg(1, 1, 2, 1)));
    }
}
