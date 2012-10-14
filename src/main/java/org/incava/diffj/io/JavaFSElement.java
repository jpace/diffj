package org.incava.diffj.io;

import java.io.File;
import org.incava.analysis.Report;
import org.incava.diffj.lang.DiffJException;

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

    protected String getSourceVersion() {
        return sourceVersion;
    }

    public abstract int compareTo(Report report, JavaFSElement elmt) throws DiffJException;

    public abstract int compareFrom(Report report, JavaFile file) throws DiffJException;

    public abstract int compareFrom(Report report, JavaDirectory dir) throws DiffJException;
}
