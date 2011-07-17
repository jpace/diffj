package org.incava.qualog;

import java.util.*;


/**
 * Wraps Enumerations for output.
 */
public class QlEnumeration {
    
    public static <T> boolean stack(QlLevel level, 
                                    ANSIColor[] msgColors,
                                    String name,
                                    Enumeration<T> en,
                                    ANSIColor fileColor,
                                    ANSIColor classColor,
                                    ANSIColor methodColor,
                                    int numFrames) {
        Collection<T> ary = Collections.list(en);

        return QlCollection.stack(level, msgColors, name, ary, fileColor, classColor, methodColor, numFrames);
    }
}

