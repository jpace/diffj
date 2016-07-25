package org.incava.diffj.io;

import java.io.File;
import org.incava.analysis.Report;
import org.incava.diffj.lang.DiffJException;

/**
 * Creates Java filesystem elements (files and directories).
 */
public class JavaElementFactory {
    public JavaFSElement createElement(File file, String label, String source, boolean recurseDirectories) throws DiffJException {
        JavaFile javaFile = createFile(file, label, source);
        if (javaFile != null) {
            return javaFile;
        }
        else if (file.isDirectory()) {
            return new JavaDirectory(file, source, recurseDirectories);
        }
        else {
            noSuchFile(file, label);
            return null;
        }
    }

    public JavaFile createFile(File file, String label, String source) throws DiffJException {
        if (file == null || file.getName().equals("-") || file.isFile() && verifyExists(file, label)) {
            return new JavaFile(file, label, source);
        }
        else {
            return null;
        }
    }

    public boolean verifyExists(File file, String label) throws DiffJException {
        if (file != null && file.exists()) {
            return true;
        }
        else {
            noSuchFile(file, label);
            return false;
        }
    }

    public void noSuchFile(File file, String label) throws DiffJException {
        throw new DiffJException(getName(file, label) + " does not exist");
    }

    public String getName(File file, String label) {
        return label == null ? file.getAbsolutePath() : label;
    }
}
