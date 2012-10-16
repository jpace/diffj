package org.incava.diffj;

import org.incava.analysis.FileDiffChange;
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
                 
                 makeModifierRef("abstract", null, loc(2, 5), loc(2, 12), loc(3, 5), loc(3, 8)),
                 new FileDiffChange(METHOD_BLOCK_ADDED, loc(2, 14), loc(2, 24), loc(3, 5), loc(3, 17)));
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
                 
                 makeModifierRef(null, "abstract", loc(2, 5), loc(2,  8), loc(3, 5), loc(3, 12)),
                 new FileDiffChange(METHOD_BLOCK_REMOVED, loc(2, 5), loc(2, 17), loc(3, 14), loc(3, 24)));
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

                 makeModifierRef("native", null, loc(2, 5), loc(2, 10), loc(3, 5), loc(3, 8)),
                 new FileDiffChange(METHOD_BLOCK_ADDED, loc(2, 12), loc(2, 22), loc(3, 5), loc(5, 5)));
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
                 
                 makeModifierRef(null, "native", loc(2, 5), loc(2, 8), loc(2, 5), loc(2, 10)),
                 new FileDiffChange(METHOD_BLOCK_REMOVED, loc(2, 5), loc(4, 5), loc(2, 12), loc(2, 22)));
    }
}
