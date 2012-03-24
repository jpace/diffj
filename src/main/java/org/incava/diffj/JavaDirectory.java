package org.incava.diffj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.incava.analysis.Report;

/**
 * A directory that may or not have Java files.
 */
public class JavaDirectory extends JavaFSElement {
    public static final long serialVersionUID = 1L;

    public JavaDirectory(String name, String sourceVersion) {
        super(name, sourceVersion);
    }

    public JavaDirectory(File file, String sourceVersion) {
        super(file.getPath(), sourceVersion);
    }

    public JavaFile createJavaFile(File file, String label) throws DiffJException {
        return new JavaFile(file, label, getSourceVersion());
    }

    public JavaDirectory createJavaDirectory(File file) {
        return new JavaDirectory(file, getSourceVersion());
    }

    public JavaFSElement getElement(String name) throws DiffJException {
        File[] files = listFiles();
        for (File file : files) {
            if (file.getName().equals(name)) {
                return file.isDirectory() ? createJavaDirectory(file) : createJavaFile(file, null);
            }
        }

        return null;
    }

    public List<String> getElementNames() {
        File[] files = listFiles();
        List<String> names = new ArrayList<String>();
        if (files == null) {
            return names;
        }
        
        for (File file : files) {
            if (file.isDirectory() || (file.isFile() && file.getName().endsWith(".java"))) {
                names.add(file.getName());
            }
        }

        return names;
    }

    public int compare(Report report, JavaFile toFile, int exitValue) throws DiffJException {
        tr.Ace.reverse("from: " + this + "; toFile: " + toFile);
        return JavaFile.compare(report, JavaFile.createFile(this, toFile), toFile, exitValue);
        // }
        // try {
        //     final boolean flushReport = true;
        //     JavaElementFactory jef = new JavaElementFactory();
        //     JavaFile fromFile = (JavaFile)jef.createElement(new File(this, toFile.getName()), null, toFile.getSourceVersion());
        //     JavaFileDiff jfd = new JavaFileDiff(report, fromFile, toFile, flushReport);
        //     return jfd.getExitValue() == 0 ? exitValue : jfd.getExitValue();
        // }
        // catch (DiffJException de) {
        //     throw de;
        // }
        // catch (Exception e) {
        //     tr.Ace.log("e", e);
        //     e.printStackTrace();
        //     throw new DiffJException(e);
        // }
    }
}
