package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * Represents an option that is an String.
 */
public class StringOption extends NonBooleanOption
{
    private String value;
    
    public StringOption(String longName, String description) {
        this(longName, description, null);
    }

    public StringOption(String longName, String description, String value) {
        super(longName, description);
        this.value = value;
    }

    /**
     * Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     */
    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    protected String getType() {
        return "string";
    }

}
