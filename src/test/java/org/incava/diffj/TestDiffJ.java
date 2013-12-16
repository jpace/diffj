package org.incava.diffj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.io.IO;
import org.incava.ijdk.lang.StringExt;
import org.incava.ijdk.util.ANSI;

public class TestDiffJ extends DiffJTest {
    public TestDiffJ(String name) {
        super(name);
        tr.Ace.setVerbose(true);
    }

    public boolean showContext() {
        return true;
    }

    // to be moved to ijdk.lang.ArrayExt

    public static List<String> appendToAll(List<String> array, String str) {
        List<String> newList = new ArrayList<String>();

        for (String element : array) {
            newList.add(element + str);
        }

        return newList;
    }

    public static String readAsString(String fName) {
        List<String> lines = IO.readLines(fName);
        return StringExt.join(appendToAll(lines, IO.EOLN), "");
    }

    public void testWithTabs() {
        List<String> expected = new ArrayList<String>();
        expected.add("- <=> -");

        expected.add("4a3 code added in TabFul()");
        expected.add("  public class TabFul {");
        expected.add("      public TabFul() {");
        expected.add("!         if (true) " + ANSI.YELLOW + "{" + ANSI.RESET);
        expected.add("              foo();");
        expected.add("          }");
        expected.add("      }");
        expected.add("");
        expected.add("5a5 code added in TabFul()");
        expected.add("      public TabFul() {");
        expected.add("          if (true) {");
        expected.add("              foo();");
        expected.add("!         " + ANSI.YELLOW + "}" + ANSI.RESET);
        expected.add("      }");
        expected.add("  }");
        expected.add("");

        tr.Ace.log("expected", expected);

        assertOutputEquals(new Lines("public class TabFul {",
                                     "    public TabFul() {",
                                     "        if (true) ",
                                     "			foo();",
                                     "	}",
                                     "}"),

                           new Lines("public class TabFul {",
                                     "    public TabFul() {",
                                     "        if (true) {",
                                     "            foo();",
                                     "        }",
                                     "    }",
                                     "}"),
                           expected);
    }

    public void testWithSpecifiedFileNames() {
        List<String> expected = new ArrayList<String>();
        expected.add("- <=> -");

        expected.add("1,2a2 method added: f()");
        expected.add("  public class FN {");
        expected.add("!     " + ANSI.YELLOW + "void f() {}" + ANSI.RESET);
        expected.add("  }");
        expected.add("");
        
        tr.Ace.log("expected", expected);

        assertOutputEquals(new Lines("public class FN {",
                                     "}"),

                           new Lines("public class FN {",
                                     "    void f() {}",
                                     "}"),
                           expected);
    }
}
