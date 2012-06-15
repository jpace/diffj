package org.incava.bar;

import java.io.*;
import java.lang.reflect.*;
import java.util.List;

/**
 * This calculates a value from a string.
 */
public abstract class Unchanged extends java.util.ArrayList {
    /**
     * The maximum length of the string, beyond which processing does not occur.
     */
    public static final int MAXIMUM_SIZE = 317;
    
    /**
     * Returns the value calculated for the string.
     */
    abstract double calculate(String str);
    
    /**
     * Does some type of calculation.
     */
    public double recalculate(int val, String str) { 
        while (val < str.length()) {
            val += calculate(str);
        }
        return val;
    }
}
