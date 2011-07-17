package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * Base class of all options, except for booleans.
 */
public abstract class NonBooleanOption extends Option
{
    public NonBooleanOption(String longName, String description) {
        super(longName, description);
    }

    /**
     * Sets from a list of command - line arguments. Returns whether this option
     * could be set from the current head of the list.
     */
    public boolean set(String arg, List<? extends Object> args) throws OptionException {
        // String arg = (String)args.get(0);

        tr.Ace.log("considering: " + arg);
        
        if (arg.equals("--" + longName)) {
            tr.Ace.log("matched long name");

            // args.remove(0);
            if (args.size() == 0) {
                throw new InvalidTypeException(longName + " expects following " + getType() + " argument");
            }
            else {
                String value = (String)args.remove(0);
                setValue(value);
            }
        }
        else if (arg.startsWith("--" + longName + "=")) {
            tr.Ace.log("matched long name + equals");

            // args.remove(0);
            int pos = ("--" + longName + "=").length();
            tr.Ace.log("position: " + pos);
            if (pos >= arg.length()) {
                throw new InvalidTypeException(longName + " expects argument of type " + getType());
            }
            else {
                String value = arg.substring(pos);
                setValue(value);
            }
        }
        else if (shortName != 0 && arg.equals("-" + shortName)) {
            tr.Ace.log("matched short name");

            // args.remove(0);
            if (args.size() == 0) {
                throw new InvalidTypeException(shortName + " expects following " + getType() + " argument");
            }
            else {
                String value = (String)args.remove(0);
                setValue(value);
            }                
        }
        else {
            tr.Ace.log("not a match");
            return false;
        }
        tr.Ace.log("matched");
        return true;
    }

    /**
     * Returns the option type.
     */
    protected abstract String getType();

}
