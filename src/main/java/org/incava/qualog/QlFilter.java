package org.incava.qualog;

import java.util.*;
import java.util.regex.*;
import org.incava.ijdk.lang.Range;


/**
 * Represents a filter for selective enabling or disabling of logging
 * statements.
 */
public class QlFilter {
    public static final Pattern NO_PATTERN = null;

    public static final Range NO_RANGE = null;
    
    private QlLevel level;

    private Pattern fileNamePat;

    private Range lineNumberRng;
    
    private Pattern classNamePat;

    private Pattern methodNamePat;

    public QlFilter(QlLevel level) {
        this.level = level;
    }

    public QlFilter(QlLevel level, Pattern fname, Range lnum, Pattern clsName, Pattern methName) {
        this.level = level;

        fileNamePat = fname;
        lineNumberRng = lnum;
        classNamePat = clsName;
        methodNamePat = methName;
    }

    public QlFilter(QlLevel level, String fname, Range lnum, String clsName, String methName) {
        this(level,
             fname == null    ? (Pattern)null : Pattern.compile(fname),
             lnum,
             clsName == null  ? (Pattern)null : Pattern.compile(clsName),
             methName == null ? (Pattern)null : Pattern.compile(methName));
    }

    /**
     * Returns the level.
     */
    public QlLevel getLevel() {
        return level;
    }

    /**
     * Returns whether the given parameters match this filter.
     */
    public boolean isMatch(String fileName, int lineNumber, String className, String methodName) {
        return ((fileNamePat   == null || fileNamePat.matcher(fileName).matches())   &&
                (lineNumberRng == null || lineNumberRng.includes(lineNumber))        &&
                (classNamePat  == null || classNamePat.matcher(className).matches()) && 
                (methodNamePat == null || methodNamePat.matcher(methodName).matches()));
    }

}
