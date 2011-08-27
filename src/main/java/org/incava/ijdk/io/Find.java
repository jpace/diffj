package org.incava.ijdk.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.incava.qualog.Qualog;


/**
 * Support for <code>find(1)</code>-like behavior.
 */
public class Find {
    /**
     * Passes back a list of files. Directories are processed recursively,
     * collecting files with the suffix of <code>suffix</code>. If
     * <code>name</code> refers to a file, it is simply added to the list.
     */
    public static void getFileList(List<String> fileList, String name, final String suffix) {
        try {
            File fd = new File(name);
            if (fd.isDirectory()) {
                Qualog.log("processing directory");
                String[] contents = fd.list(new FilenameFilter() {
                        public boolean accept(File dir, String nm) {
                            File f = new File(dir, nm);
                            return f.isDirectory() || (f.isFile() && nm.endsWith(suffix));
                        }
                    });
                for (int ci = 0; contents != null && ci < contents.length; ++ci) {
                    getFileList(fileList, name + File.separator + contents[ci], suffix);
                }
            }
            else if (fd.isFile()) {
                Qualog.log("adding: " + fd);
                fileList.add(fd.getCanonicalPath());
            }
            else {
                System.err.println(name + " not found.");
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("File " + name + " not found.");
        }
        catch (IOException e) {
            System.err.println("Error opening " + name + ": " + e);
        }
    }

    /**
     * Returns an array of files, collected from the <code>names</code> list. 
     * Directories are processed recursively, collecting files with
     * <code>suffix</code>. If <code>name</code> refers to a file, it is simply
     * added to the list.
     */
    public static String[] getFileList(String[] names, String suffix) {
        List<String> fileList = new ArrayList<String>();
        for (int i = 0; i < names.length; ++i) {
            getFileList(fileList, names[i], suffix);
        }
        return fileList.toArray(new String[fileList.size()]);
    }

}
