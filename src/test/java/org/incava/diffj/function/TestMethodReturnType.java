package org.incava.diffj.function;

import org.incava.analysis.FileDiffChange;
import org.incava.diffj.*;
import static org.incava.diffj.function.Method.*;

public class TestMethodReturnType extends ItemsTest {
    public TestMethodReturnType(String name) {
        super(name);
    }

    public void testReturnTypeChanged() {
        evaluate(new Lines("class Test {",
                           "    Object foo() { return null; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Integer foo() { return null; }",
                           "}"),
                 
                 new FileDiffChange(getMessage(null, null, RETURN_TYPE_CHANGED, "Object", "Integer"),
                                    loc(2, 5), loc(2, 10), 
                                    loc(3, 5), loc(3, 11)));
    }
}
