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
import org.incava.ijdk.io.ReadOptionType;
import org.incava.ijdk.io.ReaderExt;
import org.incava.java.Java;

/**
 * Represents a Java file in this crazy DiffJ world of ours.
 */
public class JavaFile extends JavaFSElement {
    public static final long serialVersionUID = 1L;

    private final String name;
    private final String contents;

    public JavaFile(String name, String contents, String sourceVersion) {
        super(name, sourceVersion);
        this.name = name;
        this.contents = contents;
    }

    public JavaFile(File file, String name, String sourceVersion) throws IOException {
        super(file.getPath(), sourceVersion);
        boolean isStdin = file == null || file.getName().equals("-");
        FileReader reader = isStdin ? new FileReader(FileDescriptor.in) : new FileReader(file);
        this.contents = ReaderExt.readAsString(reader, EnumSet.of(ReadOptionType.ADD_EOLNS));
        this.name = name == null ? (isStdin ? "-" : file.getPath()) : name;
    }

    /** 
     * Constructor for stdin.
     */
    public JavaFile(String name, String sourceVersion) {
        super(name == null ? "-" : name, sourceVersion);
        FileReader reader = new FileReader(FileDescriptor.in);
        this.contents = ReaderExt.readAsString(reader, EnumSet.of(ReadOptionType.ADD_EOLNS));
        this.name = name == null ? "-" : name;
    }

    public String getName() {
        return name;
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
            throw new DiffJException("Error tokenizing " + name + ": " + tme.getMessage(), tme);
        }
        catch (ParseException pe) {
            throw new DiffJException("Error parsing " + name + ": " + pe.getMessage(), pe);
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
            throw new DiffJException("Error processing " + name + ": " + e.getMessage(), e);
        }
    }
}
