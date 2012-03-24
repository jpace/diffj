package org.incava.diffj;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.EnumSet;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.TokenMgrError;
import org.incava.analysis.Report;
import org.incava.ijdk.io.ReadOptionType;
import org.incava.ijdk.io.ReaderExt;
import org.incava.java.Java;

/**
 * Represents a Java file in this crazy DiffJ world of ours.
 */
public class JavaFile extends JavaFSElement {
    public static final long serialVersionUID = 1L;

    private final String label; // not necessarily the same as the name.
    private final String contents;

    public JavaFile(String label, String contents, String sourceVersion) {
        super(label, sourceVersion);
        this.label = label;
        this.contents = contents;
    }

    public JavaFile(File file, String label, String sourceVersion) throws IOException {
        super(file.getPath(), sourceVersion);
        boolean isStdin = file == null || file.getName().equals("-");
        FileReader reader = isStdin ? new FileReader(FileDescriptor.in) : new FileReader(file);
        this.contents = ReaderExt.readAsString(reader, EnumSet.of(ReadOptionType.ADD_EOLNS));
        this.label = label == null ? (isStdin ? "-" : file.getPath()) : label;
    }

    /** 
     * Constructor for stdin.
     */
    public JavaFile(String label, String sourceVersion) {
        super(label == null ? "-" : label, sourceVersion);
        FileReader reader = new FileReader(FileDescriptor.in);
        this.contents = ReaderExt.readAsString(reader, EnumSet.of(ReadOptionType.ADD_EOLNS));
        this.label = label == null ? "-" : label;
    }

    public String getLabel() {
        return label;
    }

    public String getContents() {
        return contents;
    }

    protected JavaParser getParser() throws DiffJException {
        Reader reader = new StringReader(contents);
        JavaCharStream jcs = new JavaCharStream(reader);
        JavaParser parser = new JavaParser(jcs);
        String sourceVersion = getSourceVersion();
        
        if (sourceVersion.equals(Java.SOURCE_1_3)) {
            parser.setJDK13();
        }
        else if (sourceVersion.equals(Java.SOURCE_1_5) || sourceVersion.equals(Java.SOURCE_1_6)) {
            // no setJDK16 yet in PMD
            parser.setJDK15();
        }
        else if (!sourceVersion.equals(Java.SOURCE_1_4)) {
            throw new DiffJException("source version '" + sourceVersion + "' not recognized");
        }

        // nothing to do for 1.4 (currently the default for PMD)

        return parser;
    }

    public ASTCompilationUnit compile() throws DiffJException {
        try {
            return getParser().CompilationUnit();
        }
        catch (TokenMgrError tme) {
            throw new DiffJException("Error tokenizing " + label + ": " + tme.getMessage(), tme);
        }
        catch (ParseException pe) {
            throw new DiffJException("Error parsing " + label + ": " + pe.getMessage(), pe);
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
            throw new DiffJException("Error processing " + label + ": " + e.getMessage(), e);
        }
    }

    // public int process(Report report, JavaFSElement elmt, int exitValue) {
    //     tr.Ace.onBlue("this", this);
    //     tr.Ace.onBlue("elmt", elmt);

    //     if (elmt instanceof JavaFile) {
    //         return processFiles(report, (JavaFile)elmt, exitValue);
    //     }
    //     else {
    //         return -1;
    //     }
    // }

    public int process(Report report, JavaFile to, int exitValue) {
        tr.Ace.log("from: " + this + "; to: " + to);

        try {
            final boolean flushReport = true;
            JavaFileDiff jfd = new JavaFileDiff(report, this, to, flushReport);
            return jfd.getExitValue() == 0 ? exitValue : jfd.getExitValue();
        }
        catch (Throwable t) {
            tr.Ace.log("t", t);
            t.printStackTrace();
            return -1;
        }
    }
}
