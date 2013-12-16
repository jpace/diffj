package org.incava.diffj.function;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import org.incava.java.Java;

public class TestMethodsAnnotated extends ItemsTest {
    public TestMethodsAnnotated(String name) {
        super(name);
    }

    public void testNoChange() {
        evaluate(new Lines("public interface AS {",
                           "    @Foo(name = \"Fred\")",
                           "    Integer getFred();",
                           "}"),
                 new Lines("public interface AS {",
                           "    @Foo(name = \"Fred\")",
                           "    Integer getFred();",
                           "}"),
                 Java.SOURCE_1_6);
    }

    public void testAnnotatedToUnannotated() {
        evaluate(new Lines("public interface AS {",
                           "    @Foo(name = \"Fred\")",
                           "    Integer getFred();",
                           "}"),
                 new Lines("public interface AS {",
                           "    Integer getFred();",
                           "}"),
                 Java.SOURCE_1_6);
    }

    public void testUnannotatedToAnnotated() {
        evaluate(new Lines("public interface AS {",
                           "    Integer getFred();",
                           "}"),
                 new Lines("public interface AS {",
                           "    @Foo(name = \"Fred\")",
                           "    Integer getFred();",
                           "}"),
                 Java.SOURCE_1_6);
    }
}
