package org.incava.diffj.compunit;

import java.io.StringWriter;
import java.text.MessageFormat;
import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.*;
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
                 
                 new FileDiffDelete(IMPORT_SECTION_REMOVED.format(), locrg(1, 1, 1, 19), locrg(1, 1, 1, 5)));
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
                 
                 new FileDiffDelete(IMPORT_SECTION_REMOVED.format(), locrg(1, 1, 2, 23), locrg(1, 1, 1, 5)));
    }

    public void testImportsSectionAddedOne() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("import java.util.*;",
                           "",
                           "class Test {",
                           "}"),
                 
                 new FileDiffAdd(IMPORT_SECTION_ADDED.format(), locrg(1, 1, 1, 5), locrg(1, 1, 1, 19)));
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
                 
                 new FileDiffAdd(IMPORT_SECTION_ADDED.format(), locrg(1, 1, 1, 5), locrg(1, 1, 2, 23)));
    }

    public void testImportsBlockAddedNoClassDefined() {
        evaluate(new Lines("package org.incava.foo;",
                           ""),

                 new Lines("package org.incava.foo;",
                           "",
                           "import java.util.*;",
                           "import org.incava.Bazr;",
                           ""),

                 new FileDiffAdd(IMPORT_SECTION_ADDED.format(), locrg(1, 1, 1, 7), locrg(3, 1, 4, 23)));
    }

    public void testImportAdded() {
        String msg = IMPORT_ADDED.format("org.incava.Bazr");
        evaluate(new Lines("package org.incava.foo;",
                           "import java.util.*;",
                           ""),

                 new Lines("package org.incava.foo;",
                           "import java.util.*;",
                           "import org.incava.Bazr;",
                           ""),

                 new FileDiffAdd(msg, locrg(2, 1, 2, 19), locrg(3, 1, 3, 23)));
    }

    public void testImportRemoved() {
        String msg = IMPORT_REMOVED.format("java.io.IOException");
        evaluate(new Lines("package org.incava.foo;",
                           "import java.io.IOException;",
                           "import java.util.*;",
                           ""),

                 new Lines("package org.incava.foo;",
                           "import java.util.*;",
                           ""),

                 new FileDiffDelete(msg, locrg(2, 1, 2, 27), locrg(2, 1, 2, 19)));
    }

    public void testImportAddedAndRemoved() {
        String addMsg = IMPORT_ADDED.format("org.incava.log.Log");
        String delMsg = IMPORT_REMOVED.format("org.incava.qualog.Qualog");
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

                 new FileDiffAdd(addMsg, locrg(3, 1, 3, 32), locrg(3, 1, 3, 26)),
                 new FileDiffDelete(delMsg, locrg(3, 1, 3, 32), locrg(3, 1, 3, 26)));
    }
}
