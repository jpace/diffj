package org.incava.diffj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.incava.analysis.Report;

/**
 * A directory that may or not have Java files.
 */
public class JavaDirectory extends JavaFSElement {
    public static final long serialVersionUID = 1L;

    private final boolean canRecurse;

    public JavaDirectory(String name, String sourceVersion, boolean canRecurse) {
        super(name, sourceVersion);
        this.canRecurse = canRecurse;
    }

    public JavaDirectory(File file, String sourceVersion, boolean canRecurse) {
        this(file.getPath(), sourceVersion, canRecurse);
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
            if (file.isDirectory() || (file.isFile() && file.getName().endsWith(".java"))) {
                names.add(file.getName());
            }
        }

        return names;
    }

    public int compareTo(Report report, JavaFSElement toElmt, int exitValue) throws DiffJException {
        return toElmt.compareFrom(report, this, exitValue);
    }

    public int compareFrom(Report report, JavaFile fromFile, int exitValue) throws DiffJException {
        return JavaFile.compare(report, fromFile, JavaFile.createFile(this, fromFile), exitValue);
    }

    public int compareFrom(Report report, JavaDirectory fromDir, int exitValue) throws DiffJException {
        Set<String> names = new TreeSet<String>();
        names.addAll(fromDir.getElementNames());
        names.addAll(getElementNames());
        
        for (String name : names) {
            JavaFSElement fromElmt = fromDir.getElement(name);
            JavaFSElement toElmt = getElement(name);

            if (fromElmt != null && toElmt != null && (fromElmt.isFile() || (fromElmt.isDirectory() && canRecurse))) {
                tr.Ace.setVerbose(false);
                exitValue = fromElmt.compareTo(report, toElmt, exitValue);
                tr.Ace.setVerbose(true);
            }
        }

        return exitValue;
    }

}
