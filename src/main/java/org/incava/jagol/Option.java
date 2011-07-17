package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * Base class of all options.
 */
public abstract class Option {
    protected String longName;

    protected char shortName;

    private String description;
    
    public Option(String longName, String description) {
        this.longName = longName;
        this.description = description;
    }

    public void setShortName(char shortName) {
        this.shortName = shortName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
    
    /**
     * Returns the long option name.
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Returns the short option name.
     */
    public char getShortName() {
        return shortName;
    }

    /**
     * Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets from a list of command - line arguments. Returns whether this option
     * could be set from the current head of the list.
     */
    public abstract boolean set(String arg, List<? extends Object> args) throws OptionException;

    /**
     * Sets the value from the string, for this option type.
     */
    public abstract void setValue(String value) throws InvalidTypeException;
}
