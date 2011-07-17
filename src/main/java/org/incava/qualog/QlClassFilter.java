package org.incava.qualog;

import java.util.*;


/**
 * A filter for logging statements from a class. Unlike <code>QlFilter</code>,
 * the class under consideration is checked that it is assignable from the
 * filter class, that is, it is of the same class, or is a superclass.
 *
 * @see QlFilter
 */
public class QlClassFilter extends QlFilter {
    private Class<?> cls;

    public QlClassFilter(Class<?> cls, QlLevel level) {
        super(level);

        this.cls = cls;
    }

    /**
     * Returns whether the given parameters should be enabled for logging. By
     * default, this returns true.
     */
    public boolean isMatch(String fileName, int lineNumber, String className, String methodName) {
        try {
            Class<?> cls = Class.forName(className);
            return this.cls.isAssignableFrom(cls);
        }
        catch (ClassNotFoundException cnfe) {
            return false;
        }
    }

}
