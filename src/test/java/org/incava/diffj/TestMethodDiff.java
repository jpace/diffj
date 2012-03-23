package org.incava.diffj;

import java.text.MessageFormat;
import org.incava.analysis.FileDiffChange;
import org.incava.ijdk.text.Location;

public class TestMethodDiff extends AbstractTestItemDiff {
    protected final static String[] PARAM_MSGS = new String[] {
        MethodDiff.PARAMETER_REMOVED,
        null,
        MethodDiff.PARAMETER_ADDED,
    };

    public TestMethodDiff(String name) {
        super(name);
    }

    public void testAccessAdded() {
        evaluate(new Lines("class Test {",
                           "    void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public void foo() {}",
                           "}"),
                 
                 makeAccessRef(null, "public", loc(2, 5), loc(2, 8), loc(3, 5), loc(3, 10)));
    }

    public void testAccessRemoved() {
        evaluate(new Lines("class Test {",
                           "    public void foo() {}",
                           "",
                           "}"),
                 
                 new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),

                 makeAccessRef("public", null, loc(2, 5), loc(2, 10), loc(3, 5), loc(3, 8)));
    }

    public void testAccessChanged() {
        evaluate(new Lines("class Test {",
                           "    private void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public void foo() {}",
                           "}"),
                 
                 makeAccessRef("private", "public", loc(2, 5), loc(3, 5)));
    }

    public void testModifierAdded() {
        evaluate(new Lines("class Test {",
                           "    void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    static void foo() {}",
                           "}"),
                 
                 makeModifierRef(null, "static", loc(2, 5), loc(2, 8), loc(3, 5), loc(3, 10)));
    }

    public void testModifierAddedToExisting() {
        evaluate(new Lines("class Test {",
                           "    public void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    static public void foo() {}",
                           "}"),
                 
                 makeModifierRef(null, "static", loc(2, 5), loc(2, 10), loc(3, 5), loc(3, 10)));
    }

    public void testModifierRemoved() {
        evaluate(new Lines("class Test {",
                           "    final void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),
                 
                 makeModifierRef("final", null, loc(2, 5), loc(2, 9), loc(3, 5), loc(3, 8)));
    }

    public void testReturnTypeChanged() {
        evaluate(new Lines("class Test {",
                           "    Object foo() { return null; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Integer foo() { return null; }",
                           "}"),
                 
                 new FileDiffChange(getMessage(null, null, MethodDiff.RETURN_TYPE_CHANGED, "Object", "Integer"),
                                    loc(2, 5), loc(2, 10), 
                                    loc(3, 5), loc(3, 11)));
    }

    public void testParameterAddedNoneToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(Integer i) {}",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED, "i", loc(2, 13), loc(2, 14), loc(3, 22), loc(3, 22)));
    }

    public void testParameterAddedOneToTwo() {
        evaluate(new Lines("class Test {",
                           "    void foo(String s) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(String s, Integer i) {}",
                           "}"),

                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED, "i", loc(2, 13), loc(2, 22), loc(3, 24), loc(3, 32)));
    }

    public void testParameterAddedOneToThree() {
        evaluate(new Lines("class Test {",
                           "    void foo(String s) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(List[] ary, String s, Integer i) {}",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED, "ary", loc(2, 13), loc(2, 22), loc(3, 14), loc(3, 23)),
                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED, "i",   loc(2, 13), loc(2, 22), loc(3, 36), loc(3, 44)),
                 makeParamReorderedRef("s", 0, 1, loc(2, 21), loc(3, 33)));
    }

    public void testParameterRemovedOneToNone() {
        evaluate(new Lines("class Test {",
                           "    void foo(Integer i[][][][]) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.PARAMETER_REMOVED, "i", loc(2, 22), loc(2, 22), loc(3, 13), loc(3, 14)));
    }

    public void testParameterRemovedTwoToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo(String s, Integer i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    void foo(String s) {}",
                           "",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.PARAMETER_REMOVED, "i", loc(2, 24), loc(2, 32), loc(2, 13), loc(2, 22)));
    }

    public void testParameterRemovedThreeToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo(List[] ary, String s, Integer i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    void foo(String s) {}",
                           "",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.PARAMETER_REMOVED, "ary", loc(2, 14), loc(2, 23), loc(2, 13), loc(2, 22)),
                 makeParamReorderedRef("s", 1, 0, loc(2, 33), loc(2, 21)),
                 makeCodeChangedRef(MethodDiff.PARAMETER_REMOVED, "i",   loc(2, 36), loc(2, 44), loc(2, 13), loc(2, 22)));
    }

    public void testParameterChangedType() {
        evaluate(new Lines("class Test {",
                           "    void foo(int i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(Integer i) {}",
                           "}"),
                 
                 new FileDiffChange(getMessage(null, null, MethodDiff.PARAMETER_TYPE_CHANGED, "int", "Integer"), 
                                    loc(2, 14), loc(2, 18), 
                                    loc(3, 14), loc(3, 22)));
    }

    public void testParameterChangedName() {
        evaluate(new Lines("class Test {",
                           "    void foo(int i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(int x) {}",
                           "}"),
                 
                 new FileDiffChange(getMessage(null, null, MethodDiff.PARAMETER_NAME_CHANGED, "i", "x"),
                                    loc(2, 18), loc(2, 18), 
                                    loc(3, 18), loc(3, 18)));
    }

    public void testParameterReordered() {
        evaluate(new Lines("class Test {",
                           "    void foo(int i, double d) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(double d, int i) {}",
                           "}"),
                 
                 makeParamReorderedRef("i", 0, 1, loc(2, 18), loc(3, 28)),
                 makeParamReorderedRef("d", 1, 0, loc(2, 28), loc(3, 21)));
    }

    public void testParameterReorderedAndRenamed() {
        evaluate(new Lines("class Test {",
                           "    void foo(int i, double d) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(double dbl, int i2) {}",
                           "}"),
                 
                 new FileDiffChange(paramReordRenamedMsg("i", 0, "i2",  1), loc(2, 18), loc(2, 18), loc(3, 30), loc(3, 31)),
                 new FileDiffChange(paramReordRenamedMsg("d", 1, "dbl", 0), loc(2, 28), loc(2, 28), loc(3, 21), loc(3, 23)));
    }

    public void testParameterOneAddedOneReordered() {
        evaluate(new Lines("class Test {",
                           "    void foo(int i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(int i2, int i) {}",
                           "}"),

                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED, "i2", loc(2, 13), loc(2, 19), loc(3, 14), loc(3, 19)),
                 makeParamReorderedRef("i", 0, 1, loc(2, 18), loc(3, 26)));
    }

    public void testThrowsAddedNoneToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() throws Exception {}",
                           "}"),

                 makeCodeChangedRef(MethodDiff.THROWS_ADDED, "Exception", loc(2, 5), loc(2, 17), loc(3, 23), loc(3, 31)));
    }

    public void testThrowsAddedOneToTwo() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws IOException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() throws IOException, NullPointerException {}",
                           "}"),

                 makeCodeChangedRef(MethodDiff.THROWS_ADDED, "NullPointerException", loc(2, 23), loc(2, 33), loc(3, 36), loc(3, 55)));
    }

    public void testThrowsAddedOneToThree() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws IOException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() throws ArrayIndexOutOfBoundsException, IOException, NullPointerException {}",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.THROWS_ADDED, "ArrayIndexOutOfBoundsException", loc(2, 23), loc(2, 33), loc(3, 23), loc(3, 52)),
                 new FileDiffChange(throwsReordMsg("IOException", 0, 1), loc(2, 23), loc(2, 33), loc(3, 55), loc(3, 65)),
                 makeCodeChangedRef(MethodDiff.THROWS_ADDED, "NullPointerException", loc(2, 23), loc(2, 33), loc(3, 68), loc(3, 87)));
    }

    public void testThrowsRemovedOneToNone() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws IOException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.THROWS_REMOVED, "IOException", loc(2, 23), loc(2, 33), loc(3, 5), loc(3, 17)));
    }

    public void testThrowsRemovedTwoToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws IOException, NullPointerException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    void foo() throws IOException {}",
                           "",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.THROWS_REMOVED, "NullPointerException", loc(2, 36), loc(2, 55), loc(2, 23), loc(2, 33)));
    }

    public void testThrowsRemovedThreeToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws ArrayIndexOutOfBoundsException, IOException, NullPointerException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    void foo() throws IOException {}",
                           "",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.THROWS_REMOVED, "ArrayIndexOutOfBoundsException", loc(2, 23), loc(2, 52), loc(2, 23), loc(2, 33)),
                 new FileDiffChange(throwsReordMsg("IOException", 1, 0), loc(2, 55), loc(2, 65), loc(2, 23), loc(2, 33)),
                 makeCodeChangedRef(MethodDiff.THROWS_REMOVED, "NullPointerException", loc(2, 68), loc(2, 87), loc(2, 23), loc(2, 33)));
    }

    public void testThrowsReordered() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws ArrayIndexOutOfBoundsException, IOException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    void foo() throws IOException, ArrayIndexOutOfBoundsException {}",
                           "",
                           "}"),
                 
                 new FileDiffChange(throwsReordMsg("ArrayIndexOutOfBoundsException", 0, 1),
                                    loc(2, 23), loc(2, 52), 
                                    loc(2, 36), loc(2, 65)),
                 new FileDiffChange(throwsReordMsg("IOException", 1, 0),
                                    loc(2, 55), loc(2, 65), 
                                    loc(2, 23), loc(2, 33)));
    }

    public void testAbstractToImplementedMethod() {
        evaluate(new Lines("abstract class Test {",
                           "    abstract void foo();",
                           "",
                           "}"),

                 new Lines("abstract class Test {",
                           "",
                           "    void foo() {}",
                           "}"),
                 
                 makeModifierRef("abstract", null, loc(2, 5), loc(2, 12), loc(3, 5), loc(3, 8)),
                 new FileDiffChange(MethodDiff.METHOD_BLOCK_ADDED, loc(2, 14), loc(2, 24), loc(3, 5), loc(3, 17)));
    }

    public void testImplementedToAbstractMethod() {
        evaluate(new Lines("abstract class Test {",
                           "    void foo() {}",
                           "",
                           "}"),

                 new Lines("abstract class Test {",
                           "",
                           "    abstract void foo();",
                           "}"),
                 
                 makeModifierRef(null, "abstract", loc(2, 5), loc(2,  8), loc(3, 5), loc(3, 12)),
                 new FileDiffChange(MethodDiff.METHOD_BLOCK_REMOVED, loc(2, 5), loc(2, 17), loc(3, 14), loc(3, 24)));
    }

    public void testCodeNotChanged() {
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

    public void testCodeChanged() {
        evaluate(new Lines("class Test {",
                           "    int bar() { return -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int bar() { ",
                           "        return -2;",
                           "    }",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.CODE_CHANGED, "bar()", loc(2, 25), loc(2, 25), loc(4, 17), loc(4, 17)));
    }
    
    public void testCodeInserted() {
        evaluate(new Lines("class Test {",
                           "    int bar() { return -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int bar() { ",
                           "        int i = 0;",
                           "        return -1;",
                           "    }",
                           "}"),
                 
                 makeCodeAddedRef(MethodDiff.CODE_ADDED, "bar()", loc(2, 17), loc(2, 22), loc(4, 9), loc(4, 18)));
    }

    public void testCodeDeleted() {
        evaluate(new Lines("class Test {",
                           "    int bar() { ",
                           "        int i = 0;",
                           "        return -1;",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int bar() { return -1; }",
                           "}"),
                 
                 makeCodeDeletedRef(MethodDiff.CODE_REMOVED, "bar()", loc(3, 9), loc(3, 18), loc(3, 17), loc(3, 22)));
    }

    public void testCodeInsertedAndChanged() {
        evaluate(new Lines("class Test {",
                           "    int bar() { return -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int bar() { ",
                           "        int i = 0;",
                           "        return -2;",
                           "    }",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.CODE_CHANGED, "bar()", loc(2, 17), loc(2, 25), loc(4, 9), loc(5, 17)));
    }

    public void testMethodNativeToImplemented() {
        evaluate(new Lines("class Test {",
                           "    native void foo();",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() { ",
                           "        int i = 0;",
                           "    }",
                           "}"),

                 makeModifierRef("native", null, loc(2, 5), loc(2, 10), loc(3, 5), loc(3, 8)),
                 new FileDiffChange(MethodDiff.METHOD_BLOCK_ADDED, loc(2, 12), loc(2, 22), loc(3, 5), loc(5, 5)));
    }

    public void testMethodImplementedToNative() {
        evaluate(new Lines("class Test {",
                           "    void foo() { ",
                           "        int i = 0;",
                           "    }",
                           "}"),

                 new Lines("class Test {",
                           "    native void foo();",
                           "}"),
                 
                 makeModifierRef(null, "native", loc(2, 5), loc(2, 8), loc(2, 5), loc(2, 10)),
                 new FileDiffChange(MethodDiff.METHOD_BLOCK_REMOVED, loc(2, 5), loc(4, 5), loc(2, 12), loc(2, 22)));
    }

    // See comment in TestDiff.java, with regard to misleading LCSes.

    public void misleading_diffs_testZipDiff() {
        evaluate(new Lines("/**",
                           " * This class implements an output stream filter for writing files in the",
                           " * ZIP file format. Includes support for both compressed and uncompressed",
                           " * entries.",
                           " *",
                           " * @author	David Connelly",
                           " * @version	1.27, 02/07/03",
                           " */",
                           "    public",
                           "    class ZipOutputStream extends DeflaterOutputStream implements ZipConstants {",
                           "",
                           "        /**",
                           "         * Closes the current ZIP entry and positions the stream for writing",
                           "         * the next entry.",
                           "         * @exception ZipException if a ZIP format error has occurred",
                           "         * @exception IOException if an I/O error has occurred",
                           "         */",
                           "        public void closeEntry() throws IOException {",
                           "            ZipEntry e = entry;",
                           "            if (e != null) {",
                           "                switch (e.method) {",
                           "                    case DEFLATED:",
                           "                        if ((e.flag & 8) == 0) {",
                           "                            // verify size, compressed size, and crc-32 settings",
                           "                            if (e.size != def.getTotalIn()) {",
                           "                                throw new ZipException(",
                           "                                    \"invalid entry size (expected \" + e.size +",
                           "                                    \" but got \" + def.getTotalIn() + \" bytes)\");",
                           "                            }",
                           "                            if (e.csize != def.getTotalOut()) {",
                           "                                throw new ZipException(",
                           "                                    \"invalid entry compressed size (expected \" +",
                           "                                    e.csize + \" but got \" + def.getTotalOut() +",
                           "                                    \" bytes)\");",
                           "                            }",
                           "                            if (e.crc != crc.getValue()) {",
                           "                                throw new ZipException(",
                           "                                    \"invalid entry CRC-32 (expected 0x\" +",
                           "                                    Long.toHexString(e.crc) + \" but got 0x\" +",
                           "                                    Long.toHexString(crc.getValue()) + \")\");",
                           "                            }",
                           "                        } else {",
                           "                            e.size = def.getTotalIn();",
                           "                            e.csize = def.getTotalOut();",
                           "                            e.crc = crc.getValue();",
                           "                            writeEXT(e);",
                           "                        }",
                           "                        def.reset();",
                           "                        written += e.csize;",
                           "                        break;",
                           "                }",
                           "            }",
                           "        }",
                           "",
                           "    }"),

                 new Lines("    /**",
                           "     * This class implements an output stream filter for writing files in the",
                           "     * ZIP file format. Includes support for both compressed and uncompressed",
                           "     * entries.",
                           "     *",
                           "     * @author	David Connelly",
                           "     * @version	1.31, 12/19/03",
                           "     */",
                           "    public",
                           "        class ZipOutputStream extends DeflaterOutputStream implements ZipConstants {",
                           "",
                           "            /**",
                           "             * Closes the current ZIP entry and positions the stream for writing",
                           "             * the next entry.",
                           "             * @exception ZipException if a ZIP format error has occurred",
                           "             * @exception IOException if an I/O error has occurred",
                           "             */",
                           "            public void closeEntry() throws IOException {",
                           "                ZipEntry e = entry;",
                           "                if (e != null) {",
                           "                    switch (e.method) {",
                           "                        case DEFLATED:",
                           "                            if ((e.flag & 8) == 0) {",
                           "                                // verify size, compressed size, and crc-32 settings",
                           "                                if (e.size != def.getBytesRead()) {",
                           "                                    throw new ZipException(",
                           "                                        \"invalid entry size (expected \" + e.size +",
                           "                                        \" but got \" + def.getBytesRead() + \" bytes)\");",
                           "                                }",
                           "                                if (e.csize != def.getBytesWritten()) {",
                           "                                    throw new ZipException(",
                           "                                        \"invalid entry compressed size (expected \" +",
                           "                                        e.csize + \" but got \" + def.getBytesWritten() + \" bytes)\");",
                           "                                }",
                           "                                if (e.crc != crc.getValue()) {",
                           "                                    throw new ZipException(",
                           "                                        \"invalid entry CRC-32 (expected 0x\" +",
                           "                                        Long.toHexString(e.crc) + \" but got 0x\" +",
                           "                                        Long.toHexString(crc.getValue()) + \")\");",
                           "                                }",
                           "                            } else {",
                           "                                e.size  = def.getBytesRead();",
                           "                                e.csize = def.getBytesWritten();",
                           "                                e.crc = crc.getValue();",
                           "                                writeEXT(e);",
                           "                            }",
                           "                            def.reset();",
                           "                            written += e.csize;",
                           "                            break;",
                           "                    }",
                           "                }",
                           "            }",
                           "        }"),
                 
                 makeCodeChangedRef(MethodDiff.CODE_CHANGED, "bar()", loc(2, 17), loc(2, 25), loc(4, 9), loc(5, 17)));
    }

    public void testParameterReorderedByName() {
        evaluate(new Lines("public abstract class AbstractClass {",
                           "    public abstract String javaMethod1(final Context ctx,",
                           "        final Object[] obj1,",
                           "        final Object[] obj2);",
                           "}"),

                 new Lines("public abstract class AbstractClass {",
                           "    public abstract String",
                           "        javaMethod1(java.lang.Object obj, ",
                           "                    String string1, String string2, String string3, java.lang.Object[] obj1, java.lang.Object[] obj2);",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED,   "obj",     loc(2, 39), loc(4, 28), loc(3, 21), loc(3, 40)),
                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED,   "string1", loc(2, 39), loc(4, 28), loc(4, 21), loc(4, 34)),
                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED,   "string2", loc(2, 39), loc(4, 28), loc(4, 37), loc(4, 50)),
                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED,   "string3", loc(2, 39), loc(4, 28), loc(4, 53), loc(4, 66)),
                 makeCodeChangedRef(MethodDiff.PARAMETER_REMOVED, "ctx",     loc(2, 40), loc(2, 56), loc(3, 20), loc(4, 117)),
                 new FileDiffChange(paramReordMsg("obj1", 1, 4), loc(3, 9), loc(3, 27), loc(4, 69), loc(4, 91)),
                 new FileDiffChange(paramReordMsg("obj2", 2, 5), loc(4, 9), loc(4, 27), loc(4, 94), loc(4, 116)));
    }

    protected String paramMsg(String from, String to) {
        return getMessage(MethodDiff.PARAMETER_REMOVED,
                          MethodDiff.PARAMETER_ADDED,
                          null, 
                          from, to);
    }

    protected FileDiffChange makeParamReorderedRef(String paramName, int oldPosition, int newPosition, Location fromStart, Location toStart) {
        String msg = MessageFormat.format(MethodDiff.PARAMETER_REORDERED, paramName, oldPosition, newPosition);
        return new FileDiffChange(msg, fromStart, loc(fromStart, paramName), toStart, loc(toStart, paramName));
    }

    protected String paramReordMsg(String paramName, int oldPosition, int newPosition) {
        return MessageFormat.format(MethodDiff.PARAMETER_REORDERED, paramName, oldPosition, newPosition);
    }

    protected String paramReordRenamedMsg(String oldName, int oldPosition, String newName, int newPosition) {
        return MessageFormat.format(MethodDiff.PARAMETER_REORDERED_AND_RENAMED, oldName, oldPosition, newPosition, newName);
    }

    protected String throwsMsg(String from, String to) {
        return getMessage(MethodDiff.THROWS_REMOVED,
                          MethodDiff.THROWS_ADDED,
                          null, 
                          from, to);
    }

    protected String throwsReordMsg(String throwsName, int oldPosition, int newPosition) {
        return MessageFormat.format(MethodDiff.THROWS_REORDERED, throwsName, oldPosition, newPosition);
    }

    public void xtestIfStatementsNotChangedBracesToStatements() {
        evaluate(new Lines("public class TestMethodDiff {",
                           "    public void meth() {",
                           "        if (true)",
                           "            foo();",
                           "    }",
                           "}"),

                 new Lines("public class TestMethodDiff {",
                           "    public void meth() {",
                           "        if (true) {",
                           "            foo();",
                           "        }",
                           "    }",
                           "}"),
                 
                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED, "obj",     loc(2, 39), loc(4, 28), loc(3, 21), loc(3, 40)),
                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED, "string1", loc(2, 39), loc(4, 28), loc(4, 21), loc(4, 34)),
                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED, "string2", loc(2, 39), loc(4, 28), loc(4, 37), loc(4, 50)),
                 makeCodeChangedRef(MethodDiff.PARAMETER_ADDED, "string3", loc(2, 39), loc(4, 28), loc(4, 53), loc(4, 66)),
                 makeCodeChangedRef(MethodDiff.PARAMETER_REMOVED, "ctx",   loc(2, 40), loc(2, 56), loc(3, 20), loc(4, 117)),
                 new FileDiffChange(paramReordMsg("obj1", 1, 4), loc(3, 9), loc(3, 27), loc(4, 69), loc(4, 91)),
                 new FileDiffChange(paramReordMsg("obj2", 2, 5), loc(4, 9), loc(4, 27), loc(4, 94), loc(4, 116)));
    }
}
