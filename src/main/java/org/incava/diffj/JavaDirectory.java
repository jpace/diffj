package org.incava.diffj;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public JavaFile createJavaFile(File file, String label) {
        try {
            return new JavaFile(file, label, getSourceVersion());
        }
        catch (IOException ioe) {
            // what to do with this? ...
            tr.Ace.red("ioe", ioe);
            return null;
        }
    }

    public JavaDirectory createJavaDirectory(File file) {
        return new JavaDirectory(file, getSourceVersion());
    }

    public JavaFSElement getElement(String name) {
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
}
