package org.incava.jagol;

import java.io.*;
import java.util.*;
import org.incava.ijdk.lang.StringExt;


/**
 * Represents a list of objects that comprise this option.
 */
public class ListOption extends Option
{
    private List<String> value;
    
    /**
     * Creates the option.
     */
    public ListOption(String longName, String description) {
        this(longName, description, new ArrayList<String>());
    }

    /**
     * Creates the option, with a default list.
     */
    public ListOption(String longName, String description, List<String> value) {
        super(longName, description);
        this.value = value;
    }

    /**
     * Returns the value. This is empty by default.
     */
    public List<String> getValue() {
        return value;
    }

    /**
     * Sets the value.
     */
    public void setValue(List<String> value) {
        this.value = value;
    }

    /**
     * Sets the value from the string, for a list type. Assumes whitespace or
     * comma delimiter
     */
    public void setValue(String value) throws InvalidTypeException {
        tr.Ace.log("value: '" + value + "'");
        parse(value);
    }

    /**
     * Sets from a list of command - line arguments. Returns whether this option
     * could be set from the current head of the list. Assumes whitespace or
     * comma delimiter.
     */
    public boolean set(String arg, List<? extends Object> args) throws OptionException {
        tr.Ace.log("arg: " + arg + "; args: " + args);
     
        if (arg.equals("--" + longName)) {
            tr.Ace.log("matched long name");

            if (args.isEmpty()) {
                throw new InvalidTypeException(longName + " expects following argument");
            }
            else {
                Object value = args.remove(0);
                setValue(value.toString());
            }
        }
        else if (arg.startsWith("--" + longName + "=")) {
            tr.Ace.log("matched long name + equals");

            // args.remove(0);
            int pos = ("--" + longName + "=").length();
            tr.Ace.log("position: " + pos);
            if (pos >= arg.length()) {
                throw new InvalidTypeException(longName + " expects argument");
            }
            else {
                String value = arg.substring(pos);
                setValue(value);
            }
        }
        else if (shortName != 0 && arg.equals("-" + shortName)) {
            tr.Ace.log("matched short name");

            if (args.isEmpty()) {
                throw new InvalidTypeException(shortName + " expects following argument");
            }
            else {
                String value = args.remove(0).toString();
                setValue(value);
            }
        }
        else {
            tr.Ace.log("not a match");
            return false;
        }
        return true;
    }

    /**
     * Parses the value into the value list. If subclasses want to convert the
     * string to their own data type, override the < code > convert</code > method.
     *
     * @see ListOption#convert(String)
     */
    protected void parse(String str) throws InvalidTypeException {
        List<String> list = StringExt.listify(str);
        for (String s : list) {
            if (!s.equals("+=")) {
                value.add(convert(s));
            }
        }
    }

    /**
     * Returns the string, possibly converted to a different Object type. 
     * Subclasses can convert the string to their own data type.
     */
    protected String convert(String str) throws InvalidTypeException {
        return str;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        Iterator it = value.iterator();
        boolean isFirst = true;
        while (it.hasNext()) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                buf.append(", ");
            }
            buf.append(it.next());
        }
        return buf.toString();
    }
}
