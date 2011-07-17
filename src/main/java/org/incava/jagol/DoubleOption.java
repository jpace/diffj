package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * Represents an option that is an double.
 */
public class DoubleOption extends NonBooleanOption
{
    private Double value;
    
    public DoubleOption(String longName, String description) {
        this(longName, description, null);
    }

    public DoubleOption(String longName, String description, Double value) {
        super(longName, description);
        this.value = value;
    }

    /**
     * Returns the value. Returns null if not set.
     */
    public Double getValue() {
        return value;
    }

    /**
     * Sets the value.
     */
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * Sets the value from the string, for a double type.
     */
    public void setValue(String value) throws InvalidTypeException
    {
        tr.Ace.log("value: '" + value + "'");
        try {
            setValue(new Double(value));
        }
        catch (NumberFormatException nfe) {
            throw new InvalidTypeException(longName + " expects double argument, not '" + value + "'");
        }
    }

    public String toString() {
        return value == null ? "" : value.toString();
    }

    protected String getType() {
        return "double";
    }

}
