package org.incava.ijdk.lang;

import java.io.*;
import java.util.*;

public class IntArray extends IntegerArray {
    public int getInt(int index, int defValue) {
        Integer i = get(index);
        if (i == null) {
            return defValue;
        }
        else {
            return i.intValue();
        }
    }

    public int getInt(int index) {
        return getInt(index, 0);
    }

}
