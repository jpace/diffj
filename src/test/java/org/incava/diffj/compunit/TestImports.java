package org.incava.diffj.compunit;

import java.io.StringWriter;
import java.text.MessageFormat;
import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.lang.StringExt;
import org.incava.ijdk.text.Message;
import org.incava.java.Java;
import static org.incava.diffj.compunit.Imports.*;

public class TestImports extends ItemsTest {
    protected final static Message[] IMPORT_SECTION_MSGS = new Message[] {
        Imports.IMPORT_SECTION_REMOVED,
        null, 
        Imports.IMPORT_SECTION_ADDED,
    };

    public TestImports(String name) {
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
        evaluate(new Lines("import java.util.*;",
                           "",
                           "class Test {",
                           "",
                           "}"),

                 new Lines("import java.util.*;",
                           "class Test {",
                           "}"),

                 NO_CHANGES);
    }

    public void testImportsTwoSameOrderNoChange() {
        evaluate(new Lines("import java.util.*;",
                           "import org.incava.Bazr;",
                           "",
                           "class Test {",
                           "",
                           "}"),

                 new Lines("import java.util.*;",
                           "import org.incava.Bazr;",
                           "class Test {",
                           "}"),

                 NO_CHANGES);
    }

    public void testImportsTwoDifferentOrderNoChange() {
        evaluate(new Lines("import java.util.*;",
                           "import org.incava.Bazr;",
                           "",
                           "class Test {",
                           "",
                           "}"),

                 new Lines("import org.incava.Bazr;",
                           "import java.util.*;",
                           "class Test {",
                           "}"),

                 NO_CHANGES);
    }

    public void testImportsSectionRemovedOne() {
        evaluate(new Lines("import java.util.*;",
                           "",
                           "class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 new FileDiffDelete(locrg(1, 1, 19), locrg(1, 1, 5), IMPORT_SECTION_REMOVED));
    }

    public void testImportsSectionRemovedTwo() {
        evaluate(new Lines("import java.util.*;",
                           "import org.incava.Bazr;",
                           "",
                           "class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 new FileDiffDelete(locrg(1, 1, 2, 23), locrg(1, 1, 5), IMPORT_SECTION_REMOVED));
    }

    public void testImportsSectionAddedOne() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("import java.util.*;",
                           "",
                           "class Test {",
                           "}"),
                 
                 new FileDiffAdd(locrg(1, 1, 5), locrg(1, 1, 19), IMPORT_SECTION_ADDED));
    }

    public void testImportsSectionAddedTwo() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("import java.util.*;",
                           "import org.incava.Bazr;",
                           "",
                           "class Test {",
                           "}"),
                 
                 new FileDiffAdd(locrg(1, 1, 5), locrg(1, 1, 2, 23), IMPORT_SECTION_ADDED));
    }

    public void testImportsBlockAddedNoClassDefined() {
        evaluate(new Lines("package org.incava.foo;",
                           ""),

                 new Lines("package org.incava.foo;",
                           "",
                           "import java.util.*;",
                           "import org.incava.Bazr;",
                           ""),

                 new FileDiffAdd(locrg(1, 1, 1, 7), locrg(3, 1, 4, 23), IMPORT_SECTION_ADDED));
    }

    public void testImportAdded() {
        evaluate(new Lines("package org.incava.foo;",
                           "import java.util.*;",
                           ""),

                 new Lines("package org.incava.foo;",
                           "import java.util.*;",
                           "import org.incava.Bazr;",
                           ""),

                 new FileDiffAdd(locrg(2, 1, 2, 19), locrg(3, 1, 3, 23), IMPORT_ADDED, "org.incava.Bazr"));
    }

    public void testImportRemoved() {
        evaluate(new Lines("package org.incava.foo;",
                           "import java.io.IOException;",
                           "import java.util.*;",
                           ""),

                 new Lines("package org.incava.foo;",
                           "import java.util.*;",
                           ""),

                 new FileDiffDelete(locrg(2, 1, 2, 27), locrg(2, 1, 2, 19), IMPORT_REMOVED, "java.io.IOException"));
    }

    public void testImportAddedAndRemoved() {
        evaluate(new Lines("package org.incava.util;",
                           "",
                           "import org.incava.qualog.Qualog;",
                           "",
                           "public class TimedEvent",
                           "{",
                           "}",
                           "",
                           "\n"),

                 new Lines("package org.incava.util;",
                           "",
                           "import org.incava.log.Log;",
                           "",
                           "public class TimedEvent",
                           "{",
                           "}\n"),

                 new FileDiffAdd(locrg(3, 1, 3, 32), locrg(3, 1, 3, 26), IMPORT_ADDED, "org.incava.log.Log"),
                 new FileDiffDelete(locrg(3, 1, 3, 32), locrg(3, 1, 3, 26), IMPORT_REMOVED, "org.incava.qualog.Qualog"));
    }
}
