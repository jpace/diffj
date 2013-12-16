package org.incava.diffj.util;

import org.incava.ijdk.lang.StringExt;

public class Lines {
    private String[] lines;
    
    public Lines(String ... lines) {
        this.lines = new String[lines.length];

        for (int idx = 0; idx < lines.length; ++idx) {
            this.lines[idx] = lines[idx] + "\n";
        }
    }

    public String[] get() {
        return lines;
    }

    public String toString() {
        return StringExt.join(lines, "");
    }
}
