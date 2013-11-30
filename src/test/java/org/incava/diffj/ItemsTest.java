package org.incava.diffj;

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
    protected final static Message[] MODIFIER_MSGS = new Message[] {
        Modifiers.MODIFIER_REMOVED,
        Modifiers.MODIFIER_CHANGED,
        Modifiers.MODIFIER_ADDED,
    };

    public ItemsTest(String name) {
        super(name);
    }

    protected FileDiff makeChangedRef(LocationRange fromLoc, LocationRange toLoc, Message[] msgs, String from, String to) {
        if (to == null) {
            return new FileDiffChange(fromLoc, toLoc, msgs[0], from);
        }
        else if (from == null) {
            return new FileDiffChange(fromLoc, toLoc, msgs[2], to);
        }
        else {
            return new FileDiffChange(fromLoc, toLoc, msgs[1], from, to);
        }
    }

    protected FileDiff makeAccessAddedRef(LocationRange fromLoc, LocationRange toLoc, String added) {
        return new FileDiffChange(fromLoc, toLoc, Access.ACCESS_ADDED, added);
    }    

    protected FileDiff makeAccessRemovedRef(LocationRange fromLoc, LocationRange toLoc, String removed) {
        return new FileDiffChange(fromLoc, toLoc, Access.ACCESS_REMOVED, removed);
    }    

    protected FileDiff makeAccessChangedRef(LocationRange fromLoc, LocationRange toLoc, String from, String to) {
        return new FileDiffChange(fromLoc, toLoc, Access.ACCESS_CHANGED, from, to);
    }    

    protected FileDiff makeAccessChangedRef(Location fromStart, Location toStart, String from, String to) {
        return new FileDiffChange(locrg(fromStart, from), locrg(toStart, to), Access.ACCESS_CHANGED, from, to);
    }    

    protected FileDiff makeModifierRef(LocationRange fromLoc, LocationRange toLoc, String from, String to) {
        return makeChangedRef(fromLoc, toLoc, MODIFIER_MSGS, from, to);
    }

    protected FileDiff makeCodeChangedRef(Message codeChgMsg, String where, LocationRange fromLoc, LocationRange toLoc) {
        return makeCodeChangedRef(codeChgMsg, new String[] { where }, fromLoc, toLoc);
    }

    protected FileDiff makeCodeChangedRef(Message codeChgMsg, String[] args, LocationRange fromLoc, LocationRange toLoc) {
        return new FileDiffChange(fromLoc, toLoc, codeChgMsg, (Object[])args);
    }

    protected FileDiff makeCodeChangedRef(LocationRange fromLoc, LocationRange toLoc, Message codeChgMsg, String ... args) {
        return new FileDiffChange(fromLoc, toLoc, codeChgMsg, (Object[])args);
    }

    protected FileDiff makeCodeAddedRef(Message codeChgMsg, String where, LocationRange fromLoc, LocationRange toLoc) {
        return makeCodeAddedRef(codeChgMsg, new String[] { where }, fromLoc, toLoc);
    }

    protected FileDiff makeCodeAddedRef(Message codeChgMsg, String[] args, LocationRange fromLoc, LocationRange toLoc) {
        return new FileDiffAdd(fromLoc, toLoc, codeChgMsg, (Object[])args);
    }

    protected FileDiff makeCodeDeletedRef(Message codeChgMsg, String where, LocationRange fromLoc, LocationRange toLoc) {
        return makeCodeDeletedRef(codeChgMsg, new String[] { where }, fromLoc, toLoc);
    }

    protected FileDiff makeCodeDeletedRef(Message codeChgMsg, String[] args, LocationRange fromLoc, LocationRange toLoc) {
        return new FileDiffDelete(fromLoc, toLoc, codeChgMsg, (Object[])args);
    }
}
