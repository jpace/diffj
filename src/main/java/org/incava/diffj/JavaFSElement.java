package org.incava.diffj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
}
