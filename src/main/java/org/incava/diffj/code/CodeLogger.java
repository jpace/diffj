package org.incava.diffj.code;

public class CodeLogger {
    protected static final boolean debug = Boolean.getBoolean("diffj.debug.code");

    public static void log(String msg, Object obj) {
        if (debug) {
            tr.Ace.log(String.format("%-30s", msg), obj);
        }
    }    
}
