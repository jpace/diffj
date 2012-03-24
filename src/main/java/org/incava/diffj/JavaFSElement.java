package org.incava.diffj;

import java.io.File;
import org.incava.analysis.Report;

/**
 * A filesystem element, such as a directory or a file.
 */
public class JavaFSElement extends File {
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

    public String getBaseName() {
        return getAbsoluteFile().getName();
    }

    public int compare(Report report, JavaFSElement elmt, int exitValue) {
        tr.Ace.onBlue("this", this);
        tr.Ace.onBlue("elmt", elmt);

        return -1;
    }
}
