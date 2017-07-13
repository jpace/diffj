package org.incava.diffj.io;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.EnumSet;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.java.Java16Parser;
import net.sourceforge.pmd.lang.java.Java17Parser;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import org.incava.analysis.Report;
import org.incava.diffj.compunit.CompilationUnit;
import org.incava.diffj.compunit.Parser;
import org.incava.diffj.lang.DiffJException;
import org.incava.ijdk.io.ReadOptionType;
import org.incava.ijdk.io.ReaderExt;
import org.incava.java.Java;

/**
 * Represents a Java file.
 */
public class JavaFile extends JavaFSElement {
    public static final long serialVersionUID = 1L;

    public static JavaFile createFile(File dir, JavaFSElement otherElmt) throws DiffJException {
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
        tr.Ace.log("fromFile: " + fromFile + "; toFile: " + toFile);
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

    protected ASTCompilationUnit parse() throws Exception {
        String version = getSourceVersion();
        return new Parser().parse(label, contents, version);
    }

    public CompilationUnit compile() throws DiffJException {
        try {
            ASTCompilationUnit cu = parse();
            return cu == null ? null : new CompilationUnit(cu);
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
            CompilationUnit fromCompUnit = compile();
            CompilationUnit toCompUnit   = toFile.compile();
            
            report.reset(getLabel(), getContents(), toFile.getLabel(), toFile.getContents());

            if (fromCompUnit != null && toCompUnit != null) {
                fromCompUnit.diff(toCompUnit, report);
            }
        }
        catch (DiffJException de) {
            tr.Ace.log("de", de);
            System.err.println("Error: " + de.getMessage());
            throw de;
        }

        return 0;
    }
}
