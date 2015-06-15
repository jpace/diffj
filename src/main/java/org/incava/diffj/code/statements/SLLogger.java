package org.incava.diffj.code.statements;

public class SLLogger {
    protected static final boolean debug = Boolean.getBoolean("diffj.debug.statementlist");

    public static void log(String msg, Object obj) {
        if (debug) {
            tr.Ace.log(msg, obj);
        }
    }    
}
