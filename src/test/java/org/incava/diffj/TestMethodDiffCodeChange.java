package org.incava.diffj;

import java.awt.Point;
import java.text.MessageFormat;
import org.incava.analysis.FileDiff;


public class TestMethodDiffCodeChange extends AbstractTestItemDiff {

    public TestMethodDiffCodeChange(String name) {
        super(name);
    }

    public void xtestCodeNotChanged() {
        evaluate(new Lines("class Test {",
                           "    int bar() { return -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int bar() { ",
                           "        return -1;",
                           "    }",
                           "",
                           "}"),

                 NO_CHANGES);
    }

    public void testCodeChangedInsert() {
        getOutput(new Lines("class Test {",
                            "    int bar() { ",
                            "        if (true)",
                            "            foo();",
                            "    }",
                            "}"),

                  new Lines("class Test {",
                            "    int bar() { ",
                            "        if (true) {",
                            "            foo();",
                            "        }",
                            "    }",
                            "}"));
        // evaluate(new Lines("class Test {",
        //                    "    int bar() { return -1; }",
        //                    "",
        //                    "}"),

        //          new Lines("class Test {",
        //                    "    int bar() { return (-1); }",
        //                    "}"),
                 
        //          makeCodeChangedRef(MethodDiff.CODE_CHANGED, "bar()", loc(2, 25), loc(2, 25), loc(4, 17), loc(4, 17)));
    }

    public boolean showContext() {
        return true;
    }
}
