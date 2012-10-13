package org.incava.diffj;

import java.text.MessageFormat;
import org.incava.analysis.FileDiffChange;

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
                 
                 new FileDiffChange(getMessage(null, null, Messages.RETURN_TYPE_CHANGED, "Object", "Integer"),
                                    loc(2, 5), loc(2, 10), 
                                    loc(3, 5), loc(3, 11)));
    }
}
