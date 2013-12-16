package org.incava.diffj;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.incava.analysis.FileDiff;
import org.incava.analysis.Report;
import org.incava.diffj.util.Lines;

public abstract class OutputContextTest extends OutputTest {
    public OutputContextTest(String name) {
        super(name);
        tr.Ace.setVerbose(true);
        tr.Ace.log("name", name);
    }

    public abstract String adorn(String str, boolean isDelete);

    public boolean showContext() {
        return true;
    }

    public void testImportAdded() {
        String[] output = doImportAddedTest();
        tr.Ace.onBlue("output", output);
    }

    public void testCodeChangedSingleLine() {
        String[] output = doCodeChangedSingleLineTest();        
        tr.Ace.log("output", output);        
    }

    public void testCodeChangedMultipleLines() {
        String[] output = doCodeChangedMultipleLinesTest();        
        tr.Ace.log("output", output);

        // output doesn't have end-of-lines
        List<String> expected = new ArrayList<String>();
        expected.add("- <=> -");
        expected.add("2c2,3 code changed in Test(int)");
        expected.add("  class Test {");
        expected.add("!     Test(int i) { " + adorn("i = 1", true) + "; }");
        expected.add("  ");
        expected.add("  }");
        expected.add("");
        expected.add("  class Test {    Test(int i) { ");
        expected.add("!         " + adorn("int j = 0;", false));
        expected.add("! " + adorn("        i = 2", false) + "; ");
        expected.add("      }");
        expected.add("  }");
        expected.add("");

        assertEquals(expected, Arrays.asList(output));
    }

    public void testCodeDeleted() {
        String[] output = doCodeDeletedTest();
        
        tr.Ace.log("output", output);

        List<String> expected = new ArrayList<String>();
        expected.add("- <=> -");
        expected.add("3d3 code removed in Test()");
        expected.add("  class Test {");
        expected.add("      Test() { ");
        expected.add("!         " + adorn("int j = 0;", true));
        expected.add("          int i = -1;");
        expected.add("      }");
        expected.add("  }");
        expected.add("");

        assertEquals(expected, Arrays.asList(output));
    }

    public void testCodeAdded() {
        String[] output = doCodeAddedTest();
        
        tr.Ace.log("output", output);

        List<String> expected = new ArrayList<String>();
        expected.add("- <=> -");
        expected.add("4a5 code added in Test()");
        expected.add("  ");
        expected.add("      Test() { ");
        expected.add("          int j = 0;");
        expected.add("!         int i = -1" + adorn(", k = 666", false) + ";");
        expected.add("      }");
        expected.add("  }");
        expected.add("");

        assertEquals(expected, Arrays.asList(output));
    }

    public void testImportRemoved() {
        String[] output = doImportRemovedTest();
        
        tr.Ace.log("output", output);

        for (String line : output) {
            System.out.println(line);
        }

        List<String> expected = new ArrayList<String>();
        expected.add("- <=> -");
        expected.add("3d3 import section removed");
        expected.add("  package org.incava.util;");
        expected.add("  ");
        expected.add("! " + adorn("import org.incava.qualog.Qualog;", true));
        expected.add("  ");
        expected.add("  public class TimedEvent {}");
        expected.add("");

        assertEquals(expected, Arrays.asList(output));        
    }

    public void xtestCodeChanged() {
        evaluate(new Lines("package org.incava.util;",
                           "",
                           "/**",
                           " * Times an event, from when the object is created, until when the",
                           " * <code>end</code> method is invoked.",
                           " */",
                           "public class TimedEvent",
                           "{",
                           "    public void end()",
                           "    {",
                           // 234567890123456789012345678901234567890123456789
                           "        duration = System.currentTimeMillis() - startTime;",
                           "        set.add(duration);",
                           "    }",
                           "}",
                           "",
                           "\n"),

                 new Lines("package org.incava.util;",
                           "",
                           "/**",
                           " * Times an event, from when the object is created, until when the",
                           " * <code>end</code> method is invoked.",
                           " */",
                           "public class TimedEvent",
                           "{",
                           "    public void end()",
                           "    {",
                           "        Log.log(\"ending\");",
                           //         1         2         3         4         5         6         
                           //23456789012345678901234567890123456789012345678901234567890123456789
                           "        duration = System.currentTimeMillis() - startTime;",
                           "        set.add(duration);",
                           "        Log.log(\"ended\");",
                           "    }",
                           "}\n"),

                 new FileDiff[0]);
    }

    public Report makeReport(StringWriter writer) {
        return makeDetailedReport(writer);
    }
}
