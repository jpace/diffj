package org.incava.diffj.function;

import org.incava.diffj.*;
import static org.incava.diffj.code.Code.*;

public class TestCtorCode extends ItemsTest {
    public TestCtorCode(String name) {
        super(name);
    }

    public void testCodeNotChanged() {
        evaluate(new Lines("class Test {",
                           "    Test() { i = -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() { ",
                           "        i = -1;",
                           "    }",
                           "",
                           "}"),
                 
                 NO_CHANGES);
    }

    public void testCodeChanged() {
        evaluate(new Lines("class Test {",
                           "    Test() { int i = -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() { ",
                           "        int i = -2;",
                           "    }",
                           "}"),
                 
                 makeCodeChangedRef(CODE_CHANGED, "Test()", locrg(2, 23, 23), locrg(4, 18, 18)));
    }
    
    public void testCodeInsertedSameLine() {
        evaluate(new Lines("class Test {",
                           "    Test() { int i = -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() { ",
                           "        int j = 0;",
                           "        int i = -1;",
                           "    }",
                           "}"),
                 
                 makeCodeAddedRef(CODE_ADDED, "Test()", locrg(2, 18, 18), locrg(4, 13, 5, 11)));
    }

    public void testCodeAddedOwnLine() {
        evaluate(new Lines("class Test {",
                           "    Test() { ",
                           "        char ch;",
                           "        int i = -1;",
                           "    }",
                           "",
                           "}"),
                 
                 new Lines("class Test {",
                           "",
                           "    Test() { ",
                           "        char ch;",
                           "        if (true) { }",
                           "        int i = -1;",
                           "    }",
                           "}"),
                 
                 makeCodeAddedRef(CODE_ADDED, "Test()", locrg(4, 9, 11), locrg(5, 9, 21)));
    }

    public void testCodeDeleted() {
        evaluate(new Lines("class Test {",
                           "    Test() { ",
                           "        int j = 0;",
                           "        int i = -1;",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() { int i = -1; }",
                           "}"),
                 
                 makeCodeDeletedRef(CODE_REMOVED, "Test()", locrg(3, 13, 4, 11), locrg(3, 18, 18)));
    }
    
    public void testCodeInsertedAndChanged() {
        evaluate(new Lines("class Test {",
                           "    Test(int i) { i = 1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(int i) { ",
                           "        int j = 0;",
                           "        i = 2;",
                           "    }",
                           "}"),

                 makeCodeChangedRef(CODE_CHANGED, "Test(int)", locrg(2, 19, 23), locrg(4, 9, 5, 13)));
    }
}
