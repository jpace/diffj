package org.incava.diffj.function;

import org.incava.diffj.*;
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

    // @todo make assumption of single line, a la TestFieldDiff

    // public void testClassMethodAccessRemoved() {
    //     evaluate(new Lines("class Test {",
    //              "",
    //              "    public void foo() {}",
    //              "}"),
    //              new Lines("class Test {",
    //              "",
    //              "    void foo() {}",
    //              "}"),
    //              
    //              makeMethodRef("foo()", null, loc(3, 5), loc(3, 17), loc(1, 1), loc(3, 1)));
    // }
}
