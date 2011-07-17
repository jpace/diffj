package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * Represents an option that is an integer.
 */
public class IntegerOption extends NonBooleanOption
{
    private Integer value;
    
    public IntegerOption(String longName, String description) {
        this(longName, description, null);
    }

    public IntegerOption(String longName, String description, Integer value) {
        super(longName, description);
        this.value = value;
    }

    /**
     * Returns the value. This is null if not set.
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Sets the value.
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * Sets the value from the string, for an integer type.
     */
    public void setValue(String value) throws InvalidTypeException
    {
        try {
            setValue(new Integer(value));
        }
        catch (NumberFormatException nfe) {
            throw new InvalidTypeException(getLongName() + " expects integer argument, not '" + value + "'");
        }
    }

    public String toString() {
        return value == null ? "" : value.toString();
    }

    protected String getType() {
        return "integer";
    }

}
