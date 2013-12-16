package org.incava.diffj.function;

import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.text.LocationRange;

public class TestMethods extends ItemsTest {
    public TestMethods(String name) {
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
                 
                 new FileDiffAdd(locrg(1, 1, 3, 1), locrg(3, 5, 17), Method.METHOD_ADDED, "foo()"));
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
                 
                 new FileDiffAdd(locrg(1, 1, 4, 1), locrg(4, 5, 17), Method.METHOD_ADDED, "foo()"));
    }

    public void testClassAllMethodsRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 new FileDiffDelete(locrg(3, 5, 17), locrg(1, 1, 3, 1), Method.METHOD_REMOVED, "foo()"));
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
    //              makeMethodRef(locrg(3, 5, 17), locrg(1, 1, 3, 1), "foo()", null));
    // }
}
