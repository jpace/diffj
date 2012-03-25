package org.incava.diffj;

import java.io.File;
import org.incava.analysis.Report;

/**
 * A filesystem element, such as a directory or a file.
 */
public abstract class JavaFSElement extends File {
    public static final long serialVersionUID = 1L;

    private final String sourceVersion;

    public JavaFSElement(String name, String sourceVersion) {
        super(name);
        this.sourceVersion = sourceVersion;
    }

    public JavaFSElement(File file, String sourceVersion) {
        super(file.getPath());
        this.sourceVersion = sourceVersion;
    }

    protected String getSourceVersion() {
        return sourceVersion;
    }

    public abstract int compareTo(Report report, JavaFSElement elmt, int exitValue) throws DiffJException;

    public abstract int compareFrom(Report report, JavaFile file, int exitValue) throws DiffJException;

    public abstract int compareFrom(Report report, JavaDirectory dir, int exitValue) throws DiffJException;
}
