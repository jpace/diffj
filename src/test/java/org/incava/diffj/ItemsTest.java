package org.incava.diffj;

import java.text.MessageFormat;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffDelete;
import org.incava.ijdk.text.Location;
import static org.incava.diffj.compunit.Package.*;
import static org.incava.diffj.element.Access.*;
import static org.incava.diffj.element.Modifiers.*;
import static org.incava.diffj.field.Field.*;
import static org.incava.diffj.field.Variable.*;
import static org.incava.diffj.field.Variables.*;
import static org.incava.diffj.function.Ctor.*;
import static org.incava.diffj.function.Method.*;
import static org.incava.diffj.type.Type.*;
import static org.incava.diffj.type.Types.*;

public class ItemsTest extends DiffJTest {
    protected final static String[] METHOD_MSGS = new String[] {
        METHOD_REMOVED,
        METHOD_CHANGED, 
        METHOD_ADDED,
    };

    protected final static String[] FIELD_MSGS = new String[] {
        FIELD_REMOVED,
        null,
        FIELD_ADDED,
    };

    protected final static String[] CLASS_MSGS = new String[] {
        INNER_CLASS_REMOVED,
        null,
        INNER_CLASS_ADDED,
    };

    protected final static String[] INTERFACE_MSGS = new String[] {
        INNER_INTERFACE_REMOVED,
        null,
        INNER_INTERFACE_ADDED,
    };

    protected final static String[] CONSTRUCTOR_MSGS = new String[] {
        CONSTRUCTOR_REMOVED,
        null,
        CONSTRUCTOR_ADDED,
    };

    protected final static String[] MODIFIER_MSGS = new String[] {
        MODIFIER_REMOVED,
        MODIFIER_CHANGED,
        MODIFIER_ADDED,
    };

    protected final static String[] ACCESS_MSGS = new String[] {
        ACCESS_REMOVED,
        ACCESS_CHANGED, 
        ACCESS_ADDED,
    };

    protected final static String[] TYPES_MSGS = new String[] {
        TYPE_DECLARATION_REMOVED,
        null,
        TYPE_DECLARATION_ADDED,
    };

    protected final static String[] PACKAGE_MSGS = new String[] {
        PACKAGE_REMOVED, 
        PACKAGE_RENAMED,
        PACKAGE_ADDED,
    };

    protected final static String[] VARIABLE_MSGS = new String[] {
        VARIABLE_REMOVED,
        VARIABLE_CHANGED, 
        VARIABLE_ADDED,
    };

    public ItemsTest(String name) {
        super(name);
    }

    protected FileDiff makeFieldRef(String from, String to, Location a0, Location a1, Location b0, Location b1) {
        return makeRef(from, to, FIELD_MSGS, a0, a1, b0, b1);
    }

    protected FileDiff makeMethodRef(String from, String to, Location a0, Location a1, Location b0, Location b1) {
        return makeRef(from, to, METHOD_MSGS, a0, a1, b0, b1);
    }

    protected FileDiff makeClassRef(String from, String to, Location a0, Location a1, Location b0, Location b1) {
        return makeRef(from, to, CLASS_MSGS, a0, a1, b0, b1);
    }

    protected FileDiff makeInterfaceRef(String from, String to, Location a0, Location a1, Location b0, Location b1) {
        return makeRef(from, to, INTERFACE_MSGS, a0, a1, b0, b1);
    }

    protected FileDiff makeTypeRef(String from, String to, Location a0, Location a1, Location b0, Location b1) {
        return makeRef(from, to, TYPES_MSGS, a0, a1, b0, b1);
    }

    protected FileDiff makePackageRef(String from, String to, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeRef(from, to, PACKAGE_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makePackageRef(String from, String to, Location fromStart, Location toStart) {
        return makeRef(from, to, PACKAGE_MSGS, 
                       fromStart, loc(fromStart, from),
                       toStart,   loc(toStart,   to));
    }

    protected FileDiff makeVariableRef(String from, String to, Location a0, Location a1, Location b0, Location b1) {
        return makeRef(from, to, VARIABLE_MSGS, a0, a1, b0, b1);
    }

    protected FileDiff makeConstructorRef(String from, String to, Location a0, Location a1, Location b0, Location b1) {
        return makeRef(from, to, CONSTRUCTOR_MSGS, a0, a1, b0, b1);
    }

    protected FileDiff makeRef(String from, String to, String[] msgs, Location a0, Location a1, Location b0, Location b1) {
        String msg = null;

        if (to == null) {
            msg  = MessageFormat.format(msgs[0], from);
            return new FileDiffDelete(msg, a0, a1, b0, b1);
        }
        else if (from == null) {
            msg  = MessageFormat.format(msgs[2], to);
            return new FileDiffAdd(msg, a0, a1, b0, b1);
        }
        else {
            msg  = MessageFormat.format(msgs[1], from, to);
            return new FileDiffChange(msg, a0, a1, b0, b1);
        }
    }

    protected String getMessage(String removedMsg, String addedMsg, String changedMsg, String from, String to) {
        if (to == null) {
            return MessageFormat.format(removedMsg, from);
        }
        else if (from == null) {
            return MessageFormat.format(addedMsg, to);
        }
        else {
            return MessageFormat.format(changedMsg, from, to);
        }
    }

    protected String getFromToMessage(String msg, String ... args) {
        return MessageFormat.format(msg, (Object[])args);
    }

    protected FileDiff makeChangedRef(String from, String to, String[] msgs, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        String msg = null;
        if (to == null) {
            msg = MessageFormat.format(msgs[0], from);
        }
        else if (from == null) {
            msg = MessageFormat.format(msgs[2], to);
        }
        else {
            msg = MessageFormat.format(msgs[1], from, to);
        }

        return new FileDiffChange(msg, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeAccessRef(String from, String to, Location fromStart, Location toStart) {
        return makeChangedRef(from, to, ACCESS_MSGS, 
                              fromStart, loc(fromStart, from),
                              toStart,   loc(toStart,   to));
    }

    protected FileDiff makeAccessRef(String from, String to,
                                     Location fromStart, Location fromEnd,
                                     Location toStart, Location toEnd) {
        return makeChangedRef(from, to, ACCESS_MSGS, fromStart, fromEnd, toStart, toEnd);
    }
    
    protected FileDiff makeModifierRef(String from, String to,
                                       Location fromStart, Location fromEnd,
                                       Location toStart, Location toEnd) {
        return makeChangedRef(from, to, MODIFIER_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeChangedRef(String codeChgMsg, String where, Location a0, Location a1, Location b0, Location b1) {
        return makeCodeChangedRef(codeChgMsg, new String[] { where }, a0, a1, b0, b1);
    }

    protected FileDiff makeCodeChangedRef(String codeChgMsg, String[] args, Location a0, Location a1, Location b0, Location b1) {
        String msg = MessageFormat.format(codeChgMsg, (Object[])args);
        return new FileDiffChange(msg, a0, a1, b0, b1);
    }

    protected FileDiff makeCodeAddedRef(String codeChgMsg, String where, Location a0, Location a1, Location b0, Location b1) {
        return makeCodeAddedRef(codeChgMsg, new String[] { where }, a0, a1, b0, b1);
    }

    protected FileDiff makeCodeAddedRef(String codeChgMsg, String[] args, Location a0, Location a1, Location b0, Location b1) {
        String msg = MessageFormat.format(codeChgMsg, (Object[])args);
        return new FileDiffAdd(msg, a0, a1, b0, b1);
    }

    protected FileDiff makeCodeDeletedRef(String codeChgMsg, String where, Location a0, Location a1, Location b0, Location b1) {
        return makeCodeDeletedRef(codeChgMsg, new String[] { where }, a0, a1, b0, b1);
    }

    protected FileDiff makeCodeDeletedRef(String codeChgMsg, String[] args, Location a0, Location a1, Location b0, Location b1) {
        String msg = MessageFormat.format(codeChgMsg, (Object[])args);
        return new FileDiffDelete(msg, a0, a1, b0, b1);
    }
}
