package org.incava.diffj;

import org.incava.analysis.FileDiffChange;
import static org.incava.diffj.code.Code.*;

public class TestMethodCode extends ItemsTest {
    public TestMethodCode(String name) {
        super(name);
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
                 
                 makeCodeChangedRef(CODE_CHANGED, "bar()", loc(2, 25), loc(2, 25), loc(4, 17), loc(4, 17)));
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
                 
                 makeCodeAddedRef(CODE_ADDED, "bar()", loc(2, 17), loc(2, 22), loc(4, 9), loc(4, 18)));
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
                 
                 makeCodeDeletedRef(CODE_REMOVED, "bar()", loc(3, 9), loc(3, 18), loc(3, 17), loc(3, 22)));
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
                 
                 makeCodeChangedRef(CODE_CHANGED, "bar()", loc(2, 17), loc(2, 25), loc(4, 9), loc(5, 17)));
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
                 
                 makeCodeChangedRef(CODE_CHANGED, "bar()", loc(2, 17), loc(2, 25), loc(4, 9), loc(5, 17)));
    }

    public void xtestIfStatementsNotChangedBracesToStatements() {
        // evaluate(new Lines("public class TestMethodDiff {",
        //                    "    public void meth() {",
        //                    "        if (true)",
        //                    "            foo();",
        //                    "    }",
        //                    "}"),

        //          new Lines("public class TestMethodDiff {",
        //                    "    public void meth() {",
        //                    "        if (true) {",
        //                    "            foo();",
        //                    "        }",
        //                    "    }",
        //                    "}"),
                 
        //          makeCodeChangedRef(PARAMETER_ADDED, "obj",     loc(2, 39), loc(4, 28), loc(3, 21), loc(3, 40)),
        //          makeCodeChangedRef(PARAMETER_ADDED, "string1", loc(2, 39), loc(4, 28), loc(4, 21), loc(4, 34)),
        //          makeCodeChangedRef(PARAMETER_ADDED, "string2", loc(2, 39), loc(4, 28), loc(4, 37), loc(4, 50)),
        //          makeCodeChangedRef(PARAMETER_ADDED, "string3", loc(2, 39), loc(4, 28), loc(4, 53), loc(4, 66)),
        //          makeCodeChangedRef(PARAMETER_REMOVED, "ctx",   loc(2, 40), loc(2, 56), loc(3, 20), loc(4, 117)),
        //          new FileDiffChange(paramReordMsg("obj1", 1, 4), loc(3, 9), loc(3, 27), loc(4, 69), loc(4, 91)),
        //          new FileDiffChange(paramReordMsg("obj2", 2, 5), loc(4, 9), loc(4, 27), loc(4, 94), loc(4, 116)));
    }
}
