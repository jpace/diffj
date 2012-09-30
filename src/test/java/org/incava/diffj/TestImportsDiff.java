package org.incava.diffj;

import java.io.StringWriter;
import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffDelete;
import org.incava.ijdk.lang.StringExt;
import org.incava.java.Java;

public class TestImportsDiff extends DiffJTest {
    public TestImportsDiff(String name) {
        super(name);
    }

    public void testImportsNoneNoChange() {
        evaluate(new Lines("class Test {",
                           "}"),
                           
                 new Lines("class Test {",
                           "}"),

                 NO_CHANGES);
    }

    public void testImportsOneNoChange() {
        evaluate(new Lines("import java.foo.*;",
                           "",
                           "class Test {",
                           "",
                           "}"),

                 new Lines("import java.foo.*;",
                           "class Test {",
                           "}"),

                 NO_CHANGES);
    }

    public void testImportsTwoNoChange() {
        evaluate(new Lines("import java.foo.*;",
                           "import org.incava.Bazr;",
                           "",
                           "class Test {",
                           "",
                           "}"),

                 new Lines("import java.foo.*;",
                           "import org.incava.Bazr;",
                           "class Test {",
                           "}"),

                 NO_CHANGES);
    }

    public void testImportsSectionRemovedOne() {
        evaluate(new Lines("import java.foo.*;",
                           "",
                           "class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 new FileDiffDelete(ImportsDiff.IMPORT_SECTION_REMOVED, loc(1, 1), loc(1, 18), loc(1, 1), loc(1, 5)));
    }

    public void testImportsSectionRemovedTwo() {
        evaluate(new Lines("import java.foo.*;",
                           "import org.incava.Bazr;",
                           "",
                           "class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 new FileDiffDelete(ImportsDiff.IMPORT_SECTION_REMOVED, loc(1, 1), loc(2, 23), loc(1, 1), loc(1, 5)));
    }

    public void testImportsSectionAddedOne() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("import java.foo.*;",
                           "",
                           "class Test {",
                           "}"),
                 
                 new FileDiffAdd(ImportsDiff.IMPORT_SECTION_ADDED, loc(1, 1), loc(1, 5), loc(1, 1), loc(1, 18)));
    }

    public void testImportsSectionAddedTwo() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("import java.foo.*;",
                           "import org.incava.Bazr;",
                           "",
                           "class Test {",
                           "}"),
                 
                 new FileDiffAdd(ImportsDiff.IMPORT_SECTION_ADDED, loc(1, 1), loc(1, 5), loc(1, 1), loc(2, 23)));
    }

    public void testImportsBlockAddedNoClassDefined() {
        StringWriter writer = new StringWriter();
        evaluate(new Lines("package org.incava.foo;",
                           ""),

                 new Lines("package org.incava.foo;",
                           "",
                           "import java.foo.*;",
                           "import org.incava.Bazr;",
                           ""),

                 Java.SOURCE_1_3,
                 makeDetailedReport(writer),
                 new FileDiffAdd(ImportsDiff.IMPORT_SECTION_ADDED, loc(1, 1), loc(1, 7), loc(3, 1), loc(4, 23)));
        
        tr.Ace.setVerbose(true);
        tr.Ace.red("*******************************************************");

        String[] lines = StringExt.split(writer.getBuffer().toString(), "\n");
        System.out.println("lines: " + lines);

        tr.Ace.log("lines", lines);
    }
}
