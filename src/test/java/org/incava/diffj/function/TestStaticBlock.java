package org.incava.diffj.function;

import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.code.Code;
import org.incava.diffj.util.Lines;

public class TestStaticBlock extends ItemsTest {
    public TestStaticBlock(String name) {
        super(name);
    }

    public void testStaticBlockRemoved() {
        evaluate(new Lines("class Test {",
                           "    { int i; }",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 new FileDiffDelete(locrg(2, 5, 14), locrg(1, 1, 2, 1), Initializer.STATIC_BLOCK_REMOVED));
    }

    public void testStaticBlockAdded() {
        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("class Test {",
                           "    { int i; }",
                           "}"),
                 
                 new FileDiffAdd(locrg(1, 1, 2, 1), locrg(2, 5, 14), Initializer.STATIC_BLOCK_ADDED));
    }

    public void testStaticBlockChanged() {
        evaluate(new Lines("class Test {",
                           "    { int i; }",
                           "}"),

                 new Lines("class Test {",
                           "    { double d; }",
                           "}"),
                 
                 new FileDiffChange(locrg(2, 7, 11), locrg(2, 7, 14), Code.CODE_CHANGED, "static block"));
    }
}
