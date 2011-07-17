package org.incava.ijdk.util;

import java.io.*;
import java.util.*;

public class Arrays {
    public static boolean contains(int[] array, int value) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == value) {
                return true;
            }
        }
        return false;
    }
}
