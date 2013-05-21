package org.incava.diffj;

import java.text.MessageFormat;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.compunit.Package;
import org.incava.diffj.element.Access;
import org.incava.diffj.element.Modifiers;
import org.incava.diffj.field.Variable;
import org.incava.diffj.field.Variables;
import org.incava.diffj.function.Ctor;
import org.incava.diffj.function.Method;
import org.incava.diffj.type.Type;
import org.incava.diffj.type.Types;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;

public class ItemsTest extends DiffJTest {
    protected final static Message[] METHOD_MSGS = new Message[] {
        Method.METHOD_REMOVED,
        Method.METHOD_CHANGED, 
        Method.METHOD_ADDED,
    };

    protected final static Message[] CLASS_MSGS = new Message[] {
        Type.INNER_CLASS_REMOVED,
        null,
        Type.INNER_CLASS_ADDED,
    };

    protected final static Message[] INTERFACE_MSGS = new Message[] {
        Type.INNER_INTERFACE_REMOVED,
        null,
        Type.INNER_INTERFACE_ADDED,
    };

    protected final static Message[] CONSTRUCTOR_MSGS = new Message[] {
        Ctor.CONSTRUCTOR_REMOVED,
        null,
        Ctor.CONSTRUCTOR_ADDED,
    };

    protected final static Message[] MODIFIER_MSGS = new Message[] {
        Modifiers.MODIFIER_REMOVED,
        Modifiers.MODIFIER_CHANGED,
        Modifiers.MODIFIER_ADDED,
    };

    protected final static Message[] ACCESS_MSGS = new Message[] {
        Access.ACCESS_REMOVED,
        Access.ACCESS_CHANGED, 
        Access.ACCESS_ADDED,
    };

    protected final static Message[] TYPES_MSGS = new Message[] {
        Types.TYPE_DECLARATION_REMOVED,
        null,
        Types.TYPE_DECLARATION_ADDED,
    };

    protected final static Message[] PACKAGE_MSGS = new Message[] {
        Package.PACKAGE_REMOVED, 
        Package.PACKAGE_RENAMED,
        Package.PACKAGE_ADDED,
    };

    protected final static Message[] VARIABLE_MSGS = new Message[] {
        Variables.VARIABLE_REMOVED,
        Variables.VARIABLE_CHANGED, 
        Variables.VARIABLE_ADDED,
    };

    public ItemsTest(String name) {
        super(name);
    }

    //$$$ todo: migrate (Location, Location) to (LocationRange)
    //$$$ todo: move interfaceRef, etc, to TestInterface

    protected FileDiff makeMethodRef(String from, String to, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeRef(from, to, METHOD_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeInterfaceRef(String from, String to, LocationRange fromLoc, LocationRange toLoc) {
        return makeRef(from, to, INTERFACE_MSGS, fromLoc, toLoc);
    }

    protected FileDiff makeInterfaceRef(String from, String to, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeRef(from, to, INTERFACE_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeRef(String from, String to, String[] msgs, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        String msg = null;

        if (to == null) {
            msg = MessageFormat.format(msgs[0], from);
            return new FileDiffDelete(msg, fromStart, fromEnd, toStart, toEnd);
        }
        else if (from == null) {
            msg = MessageFormat.format(msgs[2], to);
            return new FileDiffAdd(msg, fromStart, fromEnd, toStart, toEnd);
        }
        else {
            msg = MessageFormat.format(msgs[1], from, to);
            return new FileDiffChange(msg, fromStart, fromEnd, toStart, toEnd);
        }
    }

    protected FileDiff makeRef(String from, String to, String[] msgs, LocationRange fromLoc, LocationRange toLoc) {
        String msg = null;

        if (to == null) {
            msg = MessageFormat.format(msgs[0], from);
            return new FileDiffDelete(msg, fromLoc, toLoc);
        }
        else if (from == null) {
            msg = MessageFormat.format(msgs[2], to);
            return new FileDiffAdd(msg, fromLoc, toLoc);
        }
        else {
            msg = MessageFormat.format(msgs[1], from, to);
            return new FileDiffChange(msg, fromLoc, toLoc);
        }
    }

    protected FileDiff makeRef(String from, String to, Message[] msgs, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        String msg = null;

        if (to == null) {
            msg = msgs[0].format(from);
            return new FileDiffDelete(msg, fromStart, fromEnd, toStart, toEnd);
        }
        else if (from == null) {
            msg = msgs[2].format(to);
            return new FileDiffAdd(msg, fromStart, fromEnd, toStart, toEnd);
        }
        else {
            msg = msgs[1].format(from, to);
            return new FileDiffChange(msg, fromStart, fromEnd, toStart, toEnd);
        }
    }

    protected FileDiff makeRef(String from, String to, Message[] msgs, LocationRange fromLoc, LocationRange toLoc) {
        String msg = null;

        if (to == null) {
            msg = msgs[0].format(from);
            return new FileDiffDelete(msg, fromLoc, toLoc);
        }
        else if (from == null) {
            msg = msgs[2].format(to);
            return new FileDiffAdd(msg, fromLoc, toLoc);
        }
        else {
            msg = msgs[1].format(from, to);
            return new FileDiffChange(msg, fromLoc, toLoc);
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

    protected String getMessage(Message removedMsg, Message addedMsg, Message changedMsg, String from, String to) {
        if (to == null) {
            return removedMsg.format(from);
        }
        else if (from == null) {
            return addedMsg.format(to);
        }
        else {
            return changedMsg.format(from, to);
        }
    }

    protected String getFromToMessage(String msg, String ... args) {
        return MessageFormat.format(msg, (Object[])args);
    }

    protected String getFromToMessage(Message msg, String ... args) {
        return msg.format((Object[])args);
    }

    protected FileDiff makeChangedRef(String from, String to, String[] msgs, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeChangedRef(from, to, msgs, new LocationRange(fromStart, fromEnd), new LocationRange(toStart, toEnd));
    }

    protected FileDiff makeChangedRef(String from, String to, Message[] msgs, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeChangedRef(from, to, msgs, new LocationRange(fromStart, fromEnd), new LocationRange(toStart, toEnd));
    }

    protected FileDiff makeChangedRef(String from, String to, Message[] msgs, LocationRange fromLoc, LocationRange toLoc) {
        String str = null;
        if (to == null) {
            str = msgs[0].format(from);
        }
        else if (from == null) {
            str = msgs[2].format(to);
        }
        else {
            str = msgs[1].format(from, to);
        }

        return new FileDiffChange(str, fromLoc, toLoc);
    }

    protected FileDiff makeChangedRef(String from, String to, String[] msgs, LocationRange fromLoc, LocationRange toLoc) {
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

        return new FileDiffChange(msg, fromLoc, toLoc);
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

    protected FileDiff makeAccessRef(String from, String to, LocationRange fromLoc, LocationRange toLoc) {
        return makeChangedRef(from, to, ACCESS_MSGS, fromLoc, toLoc);
    }
    
    protected FileDiff makeModifierRef(String from, String to,
                                       Location fromStart, Location fromEnd,
                                       Location toStart, Location toEnd) {
        return makeChangedRef(from, to, MODIFIER_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeChangedRef(String codeChgMsg, String where, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeCodeChangedRef(codeChgMsg, new String[] { where }, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeChangedRef(String codeChgMsg, String where, LocationRange fromLoc, LocationRange toLoc) {
        return makeCodeChangedRef(codeChgMsg, new String[] { where }, fromLoc, toLoc);
    }

    protected FileDiff makeCodeChangedRef(String codeChgMsg, String[] args, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        String msg = MessageFormat.format(codeChgMsg, (Object[])args);
        return new FileDiffChange(msg, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeChangedRef(String codeChgMsg, String[] args, LocationRange fromLoc, LocationRange toLoc) {
        String msg = MessageFormat.format(codeChgMsg, (Object[])args);
        return new FileDiffChange(msg, fromLoc, toLoc);
    }

    // -------------------------------------------------------

    protected FileDiff makeCodeChangedRef(Message codeChgMsg, String[] args, LocationRange fromLoc, LocationRange toLoc) {
        String str = codeChgMsg.format((Object[])args);
        return new FileDiffChange(str, fromLoc, toLoc);
    }

    protected FileDiff makeCodeChangedRef(Message codeChgMsg, String arg, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeCodeChangedRef(codeChgMsg, arg, new LocationRange(fromStart, fromEnd), new LocationRange(toStart, toEnd));
    }

    protected FileDiff makeCodeChangedRef(Message codeChgMsg, String where, LocationRange fromLoc, LocationRange toLoc) {
        return makeCodeChangedRef(codeChgMsg, new String[] { where }, fromLoc, toLoc);
    }

    // -------------------------------------------------------

    protected FileDiff makeCodeAddedRef(String codeChgMsg, String where, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeCodeAddedRef(codeChgMsg, new String[] { where }, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeAddedRef(String codeChgMsg, String[] args, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        String msg = MessageFormat.format(codeChgMsg, (Object[])args);
        return new FileDiffAdd(msg, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeAddedRef(Message codeChgMsg, String where, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeCodeAddedRef(codeChgMsg, new String[] { where }, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeAddedRef(Message codeChgMsg, String[] args, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        String msg = codeChgMsg.format((Object[])args);
        return new FileDiffAdd(msg, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeDeletedRef(String codeChgMsg, String where, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeCodeDeletedRef(codeChgMsg, new String[] { where }, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeDeletedRef(String codeChgMsg, String[] args, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        String msg = MessageFormat.format(codeChgMsg, (Object[])args);
        return new FileDiffDelete(msg, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeDeletedRef(Message codeChgMsg, String where, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeCodeDeletedRef(codeChgMsg, new String[] { where }, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeDeletedRef(Message codeChgMsg, String[] args, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        String msg = codeChgMsg.format((Object[])args);
        return new FileDiffDelete(msg, fromStart, fromEnd, toStart, toEnd);
    }

    // -------------------------------------------------------

}
