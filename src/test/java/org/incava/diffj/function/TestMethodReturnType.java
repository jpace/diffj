package org.incava.diffj.function;

import org.incava.analysis.FileDiffChange;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
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
                 
                 new FileDiffChange(getMessage(null, null, RETURN_TYPE_CHANGED, "Object", "Integer"), locrg(2, 5, 10), locrg(3, 5, 11)));
    }
}
