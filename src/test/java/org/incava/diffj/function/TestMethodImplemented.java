package org.incava.diffj.function;

import org.incava.analysis.FileDiffChange;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import static org.incava.diffj.function.Method.*;

public class TestMethodImplemented extends ItemsTest {
    public TestMethodImplemented(String name) {
        super(name);
    }

    public void testAbstractToImplementedMethod() {
        evaluate(new Lines("abstract class Test {",
                           "    abstract void foo();",
                           "",
                           "}"),

                 new Lines("abstract class Test {",
                           "",
                           "    void foo() {}",
                           "}"),
                 
                 makeModifierRef(locrg(2, 5, 12), locrg(3, 5, 8), "abstract", null),
                 new FileDiffChange(METHOD_BLOCK_ADDED.format(), locrg(2, 14, 24), locrg(3, 5, 17)));
    }

    public void testImplementedToAbstractMethod() {
        evaluate(new Lines("abstract class Test {",
                           "    void foo() {}",
                           "",
                           "}"),

                 new Lines("abstract class Test {",
                           "",
                           "    abstract void foo();",
                           "}"),
                 
                 makeModifierRef(locrg(2, 5, 8), locrg(3, 5, 12), null, "abstract"),
                 new FileDiffChange(METHOD_BLOCK_REMOVED.format(), locrg(2, 5, 17), locrg(3, 14, 24)));
    }

    public void testMethodNativeToImplemented() {
        evaluate(new Lines("class Test {",
                           "    native void foo();",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() { ",
                           "        int i = 0;",
                           "    }",
                           "}"),

                 makeModifierRef(locrg(2, 5, 10), locrg(3, 5, 8), "native", null),
                 new FileDiffChange(METHOD_BLOCK_ADDED.format(), locrg(2, 12, 22), locrg(3, 5, 5, 5)));
    }

    public void testMethodImplementedToNative() {
        evaluate(new Lines("class Test {",
                           "    void foo() { ",
                           "        int i = 0;",
                           "    }",
                           "}"),

                 new Lines("class Test {",
                           "    native void foo();",
                           "}"),
                 
                 makeModifierRef(locrg(2, 5, 8), locrg(2, 5, 10), null, "native"),
                 new FileDiffChange(METHOD_BLOCK_REMOVED.format(), locrg(2, 5, 4, 5), locrg(2, 12, 22)));
    }
}
