package org.incava.diffj;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
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

    public static JavaFile createFile(File dir, JavaFSElement otherElmt) throws DiffJException {
        tr.Ace.onRed("dir: " + dir + "; otherElmt: " + otherElmt);
        try {
            JavaElementFactory jef = new JavaElementFactory();
            return jef.createFile(new File(dir, otherElmt.getName()), null, otherElmt.getSourceVersion());
        }
        catch (DiffJException de) {
            throw de;
        }
        catch (Exception e) {
            tr.Ace.log("e", e);
            e.printStackTrace();
            throw new DiffJException(e);
        }
    }

    public static int compare(Report report, JavaFile fromFile, JavaFile toFile) throws DiffJException {
        tr.Ace.onRed("fromFile: " + fromFile + "; toFile: " + toFile);
        try {
            fromFile.compare(report, toFile);
        }
        catch (Exception e) {
            tr.Ace.log("e", e);
            e.printStackTrace();
            throw new DiffJException(e);
        }
        finally {
            report.flush();
        }
        return 0;
    }

    private final String label; // not necessarily the same as the name.
    private final String contents;

    protected JavaFile(File file, String label, String contents, String sourceVersion) throws DiffJException {
        super(label != null ? label : file.getPath(), sourceVersion);
        try {
            boolean isStdin = file == null || file.getName().equals("-");
            if (contents != null) {
                this.contents = contents;
            }
            else {
                FileReader reader = isStdin ? new FileReader(FileDescriptor.in) : new FileReader(file);
                this.contents = ReaderExt.readAsString(reader, EnumSet.of(ReadOptionType.ADD_EOLNS));
            }
            this.label = label == null ? (isStdin ? "-" : file.getPath()) : label;
        }
        catch (FileNotFoundException e) {
            throw new DiffJException("Error opening file '" + file.getAbsolutePath() + "': " + e.getMessage(), e);
        }
        catch (IOException e) {
            throw new DiffJException("I/O error with file '" + file.getAbsolutePath() + "': " + e.getMessage(), e);
        }
    }

    public JavaFile(String label, String contents, String sourceVersion) throws DiffJException {
        this(null, label, contents, sourceVersion);
    }

    public JavaFile(File file, String label, String sourceVersion) throws DiffJException {
        this(file, label, null, sourceVersion);
    }

    /** 
     * Constructor for stdin.
     */
    public JavaFile(String label, String sourceVersion) throws DiffJException {
        this(null, label, null, sourceVersion);
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

    public int compareTo(Report report, JavaFSElement toElmt) throws DiffJException {
        return toElmt.compareFrom(report, this);
    }

    public int compareFrom(Report report, JavaFile fromFile) throws DiffJException {
        return compare(report, fromFile, this);
    }

    public int compareFrom(Report report, JavaDirectory fromDir) throws DiffJException {
        return compare(report, createFile(fromDir, this), this);
    }

    public int compare(Report report, JavaFile toFile) throws DiffJException {
        try {
            ASTCompilationUnit fromCu = compile();
            ASTCompilationUnit toCu   = toFile.compile();
            
            report.reset(getLabel(), getContents(), toFile.getLabel(), toFile.getContents());

            if (fromCu != null && toCu != null) {
                CompilationUnit cu = new CompilationUnit(fromCu);
                cu.diff(toCu, report);
            }
        }
        catch (DiffJException de) {
            tr.Ace.red("de", de);
            System.err.println("Error: " + de.getMessage());
            throw de;
        }

        return 0;
    }
}
