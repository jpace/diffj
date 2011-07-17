package org.incava.log;

import org.incava.ijdk.util.ANSI;


public class Log extends ANSI {
    public static int fileWidth = 35;
    public static int lineWidth = 5;
    public static int funcWidth = 70;

    public static boolean verbose = false;
    static {
        String verStr = System.getProperty("verbose");
        if (verStr != null) {
            verbose = Boolean.valueOf(verStr).booleanValue();
        }
    }

    public static boolean log() {
        log(null, "", 2);
        return true;
    }

    public static boolean log(String msg) {
        log(null, msg, 2);
        return true;
    }

    public static boolean log(String[] msg) {
        for (int i = 0; i < msg.length; ++i) {
            log(null, msg[i], 2);
        }
        return true;
    }

    public static boolean log(Object whence, String msg) {
        log(whence, msg, 1);
        return true;
    }

    public static boolean log(Object whence, String msg, int depth) {
        StackTraceElement[] stack = (new Exception("")).getStackTrace();
        if (depth < stack.length) {
            display(whence, msg, stack, depth);
        }
        return true;
    }

    public static boolean stack() {
        stack(null, "", 2, 5);
        return true;
    }

    public static boolean stack(String msg) {
        stack(null, msg, 2, 5);
        return true;
    }

    public static boolean stack(int depth) {
        stack(null, "", 2, depth);
        return true;
    }

    public static boolean stack(String msg, int depth) {
        stack(null, msg, 2, depth);
        return true;
    }

    public static boolean stack(Object whence, String msg, int startDepth, int numFrames) {
        StackTraceElement[] stack = (new Exception("")).getStackTrace();
        for (int si = startDepth; si < startDepth + numFrames && si < stack.length; ++si) {
            display(whence, si == startDepth ? msg : "\"\"", stack, si);
        }
        return true;
    }

    public static boolean logColor(String color, String msg) {
        log(null, color + msg + NONE, 2);
        return true;
    }

    public static void setWidths(int file, int line, int func) {
        fileWidth = file;
        lineWidth = line;
        funcWidth = func;
    }

    protected static void display(Object whence, String msg, StackTraceElement[] stack, int stackIndex) {
        if (verbose) {
            String className  = stack[stackIndex].getClassName();
            String fileName   = stack[stackIndex].getFileName();
            int    lineNumber = stack[stackIndex].getLineNumber();
            String methodName = stack[stackIndex].getMethodName();

            // How could Sun take C and leave out printf?

            // should be:
            //     String fmt = "[%" + fileWidth + "s:%" + lineWidth + "d] {%" + funcWidth + "s} %s";
            //     String str = FormattedString.toString(fmt, fileName, lineNumber, className + "#" + methodName, msg);

            StringBuffer flBuf = new StringBuffer("[");
            flBuf.append(fileName).append(":").append(lineNumber);
            for (int fi = flBuf.length(); fi < fileWidth - 1 + lineWidth; ++fi) {
                flBuf.append(" ");
            }
            flBuf.append("] ");

            // make org.incava into ...
            StringBuffer cmBuf = new StringBuffer("{");
            if (className.startsWith("org.") || className.startsWith("com.")) {
                className = "..." + className.substring(className.indexOf('.', 5) + 1);
            }

            cmBuf.append(className).append("#").append(methodName);
            for (int ci = cmBuf.length(); ci < funcWidth - 2; ++ci) {
                cmBuf.append(" ");
            }
            cmBuf.append("} ");

            // remove ending EOLN
            while (msg.length() > 0 && "\r\n".indexOf(msg.charAt(msg.length() - 1)) != -1) {
                msg = msg.substring(0, msg.length() - 1);
            }

            String outstr = flBuf.toString() + cmBuf.toString() + msg;
            System.err.println(outstr);
        }
    }

}
