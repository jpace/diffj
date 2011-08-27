package org.incava.ijdk.io;

import java.io.*;
import java.util.*;
import org.incava.qualog.Qualog;


public class FileExt {
    /**
     * The end-of-line character/sequence for this OS.
     */
    public final static String EOLN = System.getProperty("line.separator");

    /**
     * Reads the file into a single string, which is null on error. The returned
     * string will contain end-of-line characters. The <code>arg</code> argument
     * is just so we can overload based on return type.
     */
    public static String readFile(String fileName, String arg) {
        return readFile(new File(fileName), arg);
    }

    /**
     * Reads the file into a string array, without end-of-line characters
     * (sequences). The array is null on error. The <code>arg</code> argument is
     * just so we can overload based on return type.
     */
    public static String[] readFile(String fileName, String[] arg) {
        return readFile(new File(fileName), arg);
    }

    /**
     * Reads the file into a single string, which is null on error. The
     * <code>arg</code> argument is just so we can overload based on return
     * type.
     */
    public static String readFile(File file, String arg) {
        String[] contents = readFile(file, new String[] {});
        if (contents == null) {
            return null;
        }
        else {
            StringBuffer buf      = new StringBuffer();
            String       lineSep  = System.getProperty("line.separator");
            
            for (int i = 0; contents != null && i < contents.length; ++i) {
                buf.append(contents[i] + lineSep);
            }
            
            return buf.toString();
        }
    }

    /**
     * Reads the file into a string array, without end-of-line characters
     * (sequences). The <code>arg</code> argument is just so we can overload
     * based on return type.
     */
    public static String[] readFile(File file, String[] arg) {
        try {
            BufferedReader br    = new BufferedReader(new FileReader(file));
            List<String>   lines = new ArrayList<String>();

            String in;
            while ((in = br.readLine()) != null) {
                lines.add(in);
            }

            return lines.toArray(new String[lines.size()]);
        }
        catch (Exception e) {
            Qualog.log("exception: " + e);
            return null;
        }
    }

    /**
     * Reads the file into a string array, without end-of-line characters
     * (sequences). Returns empty array on error.
     */
    public static String[] readLines(File file) {
        try {
            BufferedReader br    = new BufferedReader(new FileReader(file));
            List<String>   lines = new ArrayList<String>();

            String in;
            while ((in = br.readLine()) != null) {
                lines.add(in);
            }

            return lines.toArray(new String[lines.size()]);
        }
        catch (Exception e) {
            Qualog.log("exception: " + e);
            return new String[0];
        }
    }

    /**
     * Reads the file into a string array, without end-of-line characters
     * (sequences). Returns empty array on error.
     */
    public static String[] readLines(String fName) {
        return readLines(new File(fName));
    }

    /**
     * Reads the file into a string array, optionally with end-of-line
     * characters (sequences).
     */
    public static String read(FileReader fr, boolean eoln) {
        String lineSep = eoln ? System.getProperty("line.separator") : null;
        StringBuffer contents = new StringBuffer();
        
        try {
            BufferedReader br = new BufferedReader(fr);

            String in;
            while ((in = br.readLine()) != null) {
                // contents.append(in + System.getProperty("line.separator"));
                contents.append(in);
                if (eoln) {
                    contents.append(lineSep);
                }
            }

            return contents.toString();
        }
        catch (Exception e) {
            Qualog.log("exception: " + e);
            return null;
        }
    }

}
