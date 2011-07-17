package org.incava.ijdk.io;

import java.io.*;
import java.util.*;


public class ReaderExt {
    
    public static String[] readlines(Reader rdr) {
        try {
            List<String>   lines = new ArrayList<String>();
            BufferedReader br    = new BufferedReader(rdr);
            
            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
            
            return lines.toArray(new String[lines.size()]);
        }
        catch (IOException ioe) {
            tr.Ace.log("error reading source: " + ioe);
            return new String[0];
        }
    }
}
