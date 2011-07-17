package org.incava.qualog;

import java.io.*;
import java.util.*;


/**
 * Wraps Java maps for output.
 */
public class QlMap {
    public static boolean stack(QlLevel level, 
                                ANSIColor[] msgColors,
                                String name,
                                Map map,
                                ANSIColor fileColor,
                                ANSIColor classColor,
                                ANSIColor methodColor,
                                int numFrames) {
        Set keySet = map.keySet();
        Object[] keys = keySet.toArray();
        
        if (keys.length == 0) {
            return Qualog.stack(level, msgColors, name, "()", fileColor, classColor, methodColor, numFrames);
        }
        else {
            boolean ret = true;
            for (int ki = 0; ki < keys.length; ++ki) {
                int nFrames = ki == keys.length - 1 ? numFrames : 1;
                ret = Qualog.stack(level, msgColors, name + "[" + keys[ki] + "]", map.get(keys[ki]), fileColor, classColor, methodColor, nFrames);
            }
            return ret;
        }
    }
}

