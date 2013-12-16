package org.incava.diffj;

import org.incava.diffj.util.Lines;

public class OutputTest extends ItemsTest {
    public OutputTest(String name) {
        super(name);
    }

    public boolean showContext() {
        return false;
    }

    public String[] doImportAddedTest() {
        return getOutput("package org.incava.util;\n" +
                         "\n" +
                         "public class TimedEvent\n" +
                         "{\n" +
                         "}\n" +
                         "\n" +
                         "\n",

                         "package org.incava.util;\n" +
                         "\n" +
                         "import org.incava.log.Log;\n" +
                         "\n" +
                         "public class TimedEvent\n" +
                         "{\n" +
                         "}\n");
    }

    public String[] doCodeChangedSingleLineTest() {
        return getOutput("public class Foo\n" +
                         "{\n" +
                         "    int i = 14;\n" +
                         "}\n" +
                         "\n",

                         "public class Foo\n" +
                         "{\n" +
                         "    int j = 14;\n" +
                         "}\n" +
                         "\n");
    }

    public String[] doCodeChangedMultipleLinesTest() {
        return getOutput(new Lines("class Test {",
                                   "    Test(int i) { i = 1; }",
                                   "",
                                   "}"),
                         
                         new Lines("class Test {" +
                                   "" +
                                   "    Test(int i) { ",
                                   "        int j = 0;",
                                   "        i = 2; ",
                                   "    }",
                                   "}"));
    }

    public String[] doCodeDeletedTest() {
        return getOutput(new Lines("class Test {",
                                   "    Test() { ",
                                   "        int j = 0;",
                                   "        int i = -1;",
                                   "    }",
                                   "}"),
                         
                         new Lines("class Test {",
                                   "",
                                   "    Test() { int i = -1; }",
                                   "}"));
    }

    public String[] doCodeAddedTest() {
        return getOutput(new Lines("class Test {",
                                   "    Test() { ",
                                   "        int j = 0;",
                                   "        int i = -1;",
                                   "    }",
                                   "}"),
                         
                         new Lines("class Test {",
                                   "",
                                   "    Test() { ",
                                   "        int j = 0;",
                                   "        int i = -1, k = 666;",
                                   "    }",
                                   "}"));
    }

    public String[] doImportRemovedTest() {
        return getOutput(new Lines("package org.incava.util;",
                                   "",
                                   "import org.incava.qualog.Qualog;",
                                   "",
                                   "public class TimedEvent {}"),

                         new Lines("package org.incava.util;",
                                   "",
                                   "public class TimedEvent {}"));
    }

    public void xtestCodeChanged() {
        getOutput("package org.incava.util;\n" +
                  "\n" +
                  "/**\n" +
                  " * Times an event, from when the object is created, until when the\n" +
                  " * <code>end</code> method is invoked.\n" +
                  " */\n" +
                  "public class TimedEvent\n" +
                  "{\n" +
                  "    public void end()\n" +
                  "    {\n" +
                  // 234567890123456789012345678901234567890123456789
                  "        duration = System.currentTimeMillis() - startTime;\n" +
                  "        set.add(duration);\n" +
                  "    }\n" +
                  "}\n" +
                  "\n" +
                  "\n",

                  "package org.incava.util;\n" +
                  "\n" +
                  "/**\n" +
                  " * Times an event, from when the object is created, until when the\n" +
                  " * <code>end</code> method is invoked.\n" +
                  " */\n" +
                  "public class TimedEvent\n" +
                  "{\n" +
                  "    public void end()\n" +
                  "    {\n" +
                  "        Log.log(\"ending\");\n" +
                  //         1         2         3         4         5         6         
                  //23456789012345678901234567890123456789012345678901234567890123456789
                  "        duration = System.currentTimeMillis() - startTime;\n" +
                  "        set.add(duration);\n" +
                  "        Log.log(\"ended\");\n" +
                  "    }\n" +
                  "}\n");
    }
}
