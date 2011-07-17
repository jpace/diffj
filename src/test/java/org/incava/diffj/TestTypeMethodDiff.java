package org.incava.diffj;

import java.awt.Point;
import java.text.MessageFormat;
import org.incava.java.*;


public class TestTypeMethodDiff extends AbstractTestItemDiff {

    protected final static String[] METHOD_MSGS = TestTypeDiff.METHOD_MSGS;

    public TestTypeMethodDiff(String name) {
        super(name);
    }

    public void testClassAllMethodsAdded() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),
                 
                 makeRef(null, "foo()", METHOD_MSGS, loc(1, 1), loc(3, 1), loc(3, 5), loc(3, 17)));
    }

    public void testClassOneMethodAdded() {
        evaluate(new Lines("class Test {",
                           "    int bar() { return -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int bar() { return -1; }",
                           "    void foo() {}",
                           "}"),
                 
                 makeRef(null, "foo()", METHOD_MSGS, loc(1, 1), loc(4, 1), loc(4, 5), loc(4, 17)));
    }

    public void testClassAllMethodsRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeRef("foo()", null, METHOD_MSGS, loc(3, 5), loc(3, 17), loc(1, 1), loc(3, 1)));
    }

    public void testClassNoMethodsChanged() {
        evaluate(new Lines("class Test {",
                           "    void foo() {}",
                           "    int bar() { return -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int bar() { ",
                           "        return -1;",
                           "    }",
                           "",
                           "    void foo() {}",
                           "}"),

                 NO_CHANGES);
    }

    public void xtestWithAnnotations() {
        evaluate(new Lines("public interface AS",
                           "{",
                           "  @Foo(name = \"Fred\")",
                           "  Integer getFred();",
                           "",
                           "  void setFred(Integer fred);",
                           "",
                           "  @Foo(name = \"Ginger\", targetType = Integer.class)",
                           "  Collection<Integer> getGinger();",
                           "",
                           "  void setGinger(Collection<Integer> ginger);",
                           "}"),

                 new Lines("public interface AS",
                           "{",
                           "  @Foo(name = \"Fred\", targetType = Integer.class)",
                           "  Collection<Integer> getFred();",
                           "",
                           "  void setFred(Collection<Integer> fred);",
                           "",
                           "  @Foo(name = \"Ginger\", targetType = Integer.class)",
                           "  Collection<Integer> getGingers();",
                           "",
                           "  void setGingers(Collection<Integer> Gingers);",
                           "}"),

                 Java.SOURCE_1_5);

        // FileDiff ...
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
    //              makeRef("foo()", null, METHOD_MSGS, loc(3, 5), loc(3, 17), loc(1, 1), loc(3, 1)));
    // }
}
