package org.incava.qualog;

import java.util.*;


/**
 * Provides constants that produce colorized output on ANSI terminals.
 */
public class ANSIColor {

    public final static ANSIColor NONE = new ANSIColor(0);
    public final static ANSIColor RESET = new ANSIColor(0);
    public final static ANSIColor BOLD = new ANSIColor(1);
    public final static ANSIColor UNDERSCORE = new ANSIColor(4);
    public final static ANSIColor UNDERLINE = new ANSIColor(4);
    public final static ANSIColor BLINK = new ANSIColor(5);
    public final static ANSIColor REVERSE = new ANSIColor(7);
    public final static ANSIColor CONCEALED = new ANSIColor(8);
    public final static ANSIColor BLACK = new ANSIColor(30);
    public final static ANSIColor RED = new ANSIColor(31);
    public final static ANSIColor GREEN = new ANSIColor(32);
    public final static ANSIColor YELLOW = new ANSIColor(33);
    public final static ANSIColor BLUE = new ANSIColor(34);
    public final static ANSIColor MAGENTA = new ANSIColor(35);
    public final static ANSIColor CYAN = new ANSIColor(36);
    public final static ANSIColor WHITE = new ANSIColor(37);
    public final static ANSIColor ON_BLACK = new ANSIColor(40);
    public final static ANSIColor ON_RED = new ANSIColor(41);
    public final static ANSIColor ON_GREEN = new ANSIColor(42);
    public final static ANSIColor ON_YELLOW = new ANSIColor(43);
    public final static ANSIColor ON_BLUE = new ANSIColor(44);
    public final static ANSIColor ON_MAGENTA = new ANSIColor(45);
    public final static ANSIColor ON_CYAN = new ANSIColor(46);
    public final static ANSIColor ON_WHITE = new ANSIColor(47);

    private String str;

    public ANSIColor(int n) {
        // this behavior changed in Java 1.4.2-01, so this is a char, not a
        // byte.
        str = "" + (char)27 + "[" + n + "m";
    }

    public String toString() {
        return str;
    }

}
