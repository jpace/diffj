package org.incava.diffj;

import java.text.MessageFormat;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffDelete;
import org.incava.ijdk.text.Location;

public class AbstractTestItemDiff extends AbstractDiffJTest {
    protected final static String[] METHOD_MSGS = new String[] {
        TypeDiff.METHOD_REMOVED,
        TypeDiff.METHOD_CHANGED, 
        TypeDiff.METHOD_ADDED,
    };

    protected final static String[] FIELD_MSGS = new String[] {
        TypeDiff.FIELD_REMOVED,
        null,
        TypeDiff.FIELD_ADDED,
    };

    protected final static String[] CLASS_MSGS = new String[] {
        TypeDiff.INNER_CLASS_REMOVED,
        null,
        TypeDiff.INNER_CLASS_ADDED,
    };

    protected final static String[] INTERFACE_MSGS = new String[] {
        TypeDiff.INNER_INTERFACE_REMOVED,
        null,
        TypeDiff.INNER_INTERFACE_ADDED,
    };

    protected final static String[] CONSTRUCTOR_MSGS = new String[] {
        TypeDiff.CONSTRUCTOR_REMOVED,
        null,
        TypeDiff.CONSTRUCTOR_ADDED,
    };

    protected final static String[] MODIFIER_MSGS = new String[] {
        ItemDiff.MODIFIER_REMOVED,
        ItemDiff.MODIFIER_CHANGED,
        ItemDiff.MODIFIER_ADDED,
    };

    protected final static String[] ACCESS_MSGS = new String[] {
        ItemDiff.ACCESS_REMOVED,
        ItemDiff.ACCESS_CHANGED, 
        ItemDiff.ACCESS_ADDED,
    };

    protected final static String[] TYPES_MSGS = new String[] {
        TypesDiff.TYPE_DECLARATION_REMOVED,
        null,
        TypesDiff.TYPE_DECLARATION_ADDED,
    };

    protected final static String[] PACKAGE_MSGS = new String[] {
        PackageDiff.PACKAGE_REMOVED, 
        PackageDiff.PACKAGE_RENAMED,
        PackageDiff.PACKAGE_ADDED,
    };

    protected final static String[] VARIABLE_MSGS = new String[] {
        FieldDiff.VARIABLE_REMOVED,
        FieldDiff.VARIABLE_CHANGED, 
        FieldDiff.VARIABLE_ADDED,
    };

    public AbstractTestItemDiff(String name) {
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
        String msg = null;
        if (to == null) {
            msg = MessageFormat.format(removedMsg, from);
        }
        else if (from == null) {
            msg = MessageFormat.format(addedMsg, to);
        }
        else {
            msg = MessageFormat.format(changedMsg, from, to);
        }
        return msg;
    }

    protected String getFromToMessage(String msg, String ... args) {
        return MessageFormat.format(msg, (Object[])args);
    }

    protected FileDiff makeChangedRef(String from, String to, String[] msgs, Location a0, Location a1, Location b0, Location b1) {
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

        return new FileDiffChange(msg, a0, a1, b0, b1);
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
