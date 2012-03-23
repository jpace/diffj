package org.incava.diffj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    public List<String> getSubDirsAndJavaFiles() {
        List <String> subdirs = Arrays.asList(list(new FilenameFilter() {
                public boolean accept(File dir, String pathname) {
                    File f = new File(dir, pathname);
                    return f.isDirectory() || (f.isFile() && pathname.endsWith(".java"));
                }
            }));
        tr.Ace.onBlue("subdirs", subdirs);
        return subdirs;
    }

    public List<JavaFSElement> subelements() {
        String[] contents = list();
        List<JavaFSElement> subelements = new ArrayList<JavaFSElement>();
        if (contents == null) {
            return subelements;
        }

        String sourceVersion = getSourceVersion();

        for (String c : contents) {
            File f = new File(this, c);
            if (f.isDirectory()) {
                subelements.add(new JavaDirectory(new File(this, c), sourceVersion));
            }
            else if (f.isFile() && c.endsWith(".java")) {
                // null == name (does that make sense for a directory?)
                try {
                    subelements.add(new JavaFile(new File(this, c), null, sourceVersion));
                }
                catch (IOException ioe) {
                    // what to do with this? ...
                    tr.Ace.red("ioe", ioe);
                }
            }
        }
        return subelements;
    }
}
