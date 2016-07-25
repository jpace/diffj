package org.incava.diffj.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.incava.analysis.Report;
import org.incava.diffj.lang.DiffJException;

/**
 * A directory that may or not have Java files.
 */
public class JavaDirectory extends JavaFSElement {
    public static final long serialVersionUID = 1L;

    private final boolean canRecurse;

    public JavaDirectory(File file, String sourceVersion, boolean canRecurse) {
        super(file.getPath(), sourceVersion);
        this.canRecurse = canRecurse;
    }

    public JavaFile createJavaFile(File file, String label) throws DiffJException {
        return new JavaFile(file, label, getSourceVersion());
    }

    public JavaDirectory createJavaDirectory(File file) {
        return new JavaDirectory(file, getSourceVersion(), canRecurse);
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
            if (file.isDirectory() || file.isFile() && file.getName().endsWith(".java")) {
                names.add(file.getName());
            }
        }

        return names;
    }

    public int compareTo(Report report, JavaFSElement toElmt) throws DiffJException {
        return toElmt.compareFrom(report, this);
    }

    public int compareFromFile(Report report, JavaFile fromFile) throws DiffJException {
        return JavaFile.compare(report, fromFile, JavaFile.createFile(this, fromFile));
    }

    public int compareFromDirectory(Report report, JavaDirectory fromDir) throws DiffJException {
        Set<String> names = new TreeSet<String>();
        names.addAll(fromDir.getElementNames());
        names.addAll(getElementNames());
        
        for (String name : names) {
            JavaFSElement fromElmt = fromDir.getElement(name);
            JavaFSElement toElmt = getElement(name);

            if (fromElmt != null && toElmt != null && (fromElmt.isFile() || fromElmt.isDirectory() && canRecurse)) {
                fromElmt.compareTo(report, toElmt);
            }
        }

        return 0;
    }

    public int compareFrom(Report report, JavaFile fromFile) throws DiffJException {
        return compareFromFile(report, fromFile);
    }

    public int compareFrom(Report report, JavaDirectory fromDir) throws DiffJException {
        return compareFromDirectory(report, fromDir);
    }
}
