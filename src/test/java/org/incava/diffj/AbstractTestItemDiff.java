package org.incava.diffj;

import java.awt.Point;
import java.text.MessageFormat;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffDelete;

public class AbstractTestItemDiff extends AbstractDiffJTest {
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

    public AbstractTestItemDiff(String name) {
        super(name);
    }

    protected FileDiff makeRef(String from, String to, String[] msgs, Point a0, Point a1, Point b0, Point b1) {
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

    protected FileDiff makeChangedRef(String from, String to, String[] msgs, Point a0, Point a1, Point b0, Point b1) {
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

    protected FileDiff makeAccessRef(String from, String to, Point fromStart, Point toStart) {
        return makeChangedRef(from, to, ACCESS_MSGS, 
                              fromStart, loc(fromStart, from),
                              toStart,   loc(toStart,   to));
    }

    protected FileDiff makeAccessRef(String from, String to,
                                     Point fromStart, Point fromEnd,
                                     Point toStart, Point toEnd) {
        return makeChangedRef(from, to, ACCESS_MSGS, fromStart, fromEnd, toStart, toEnd);
    }
    
    protected FileDiff makeModifierRef(String from, String to,
                                       Point fromStart, Point fromEnd,
                                       Point toStart, Point toEnd) {
        return makeChangedRef(from, to, MODIFIER_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeCodeChangedRef(String codeChgMsg, String where, Point a0, Point a1, Point b0, Point b1) {
        return makeCodeChangedRef(codeChgMsg, new String[] { where }, a0, a1, b0, b1);
    }

    protected FileDiff makeCodeChangedRef(String codeChgMsg, String[] args, Point a0, Point a1, Point b0, Point b1) {
        String msg = MessageFormat.format(codeChgMsg, (Object[])args);
        return new FileDiffChange(msg, a0, a1, b0, b1);
    }

    protected FileDiff makeCodeAddedRef(String codeChgMsg, String where, Point a0, Point a1, Point b0, Point b1) {
        return makeCodeAddedRef(codeChgMsg, new String[] { where }, a0, a1, b0, b1);
    }

    protected FileDiff makeCodeAddedRef(String codeChgMsg, String[] args, Point a0, Point a1, Point b0, Point b1) {
        String msg = MessageFormat.format(codeChgMsg, (Object[])args);
        return new FileDiffAdd(msg, a0, a1, b0, b1);
    }

    protected FileDiff makeCodeDeletedRef(String codeChgMsg, String where, Point a0, Point a1, Point b0, Point b1) {
        return makeCodeDeletedRef(codeChgMsg, new String[] { where }, a0, a1, b0, b1);
    }

    protected FileDiff makeCodeDeletedRef(String codeChgMsg, String[] args, Point a0, Point a1, Point b0, Point b1) {
        String msg = MessageFormat.format(codeChgMsg, (Object[])args);
        return new FileDiffDelete(msg, a0, a1, b0, b1);
    }
}
