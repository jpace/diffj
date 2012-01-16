package org.incava.diffj;

import java.awt.Point;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffCodeAdded;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.analysis.Report;
import org.incava.ijdk.util.*;
import org.incava.ijdk.util.diff.Diff;
import org.incava.ijdk.util.diff.Difference;
import org.incava.pmdx.*;

public class ItemDiff extends DiffComparator {    
    public static final String MODIFIER_REMOVED = "modifier removed: {0}";
    public static final String MODIFIER_ADDED = "modifier added: {0}";
    public static final String MODIFIER_CHANGED = "modifier changed from {0} to {1}";

    public static final String ACCESS_REMOVED = "access removed: {0}";
    public static final String ACCESS_ADDED = "access added: {0}";
    public static final String ACCESS_CHANGED = "access changed from {0} to {1}";
    
    public static final String CODE_CHANGED = "code changed in {0}";
    public static final String CODE_ADDED = "code added in {0}";
    public static final String CODE_REMOVED = "code removed in {0}";

    class TokenComparator extends DefaultComparator<Token> {
        public int doCompare(Token xt, Token yt) {
            int cmp = xt.kind < yt.kind ? -1 : (xt.kind > yt.kind ? 1 : 0);
            if (cmp == 0) {
                cmp = xt.image.compareTo(yt.image);
            }
            return cmp;
        }
    }

    public ItemDiff(Report report) {
        super(report);
    }

    public ItemDiff(Collection<FileDiff> differences) {
        super(differences);
    }

    /**
     * Returns a map from token types ("kinds", as java.lang.Integers), to the
     * token. This assumes that there are no leadking tokens of the same type
     * for the given node.
     */
    protected Map<Integer, Token> getModifierMap(SimpleNode node) {
        List<Token>         mods   = SimpleNodeUtil.getLeadingTokens(node);        
        Map<Integer, Token> byKind = new HashMap<Integer, Token>();

        for (Token tk : mods) {
            byKind.put(Integer.valueOf(tk.kind), tk);
        }

        return byKind;
    }

    public void compareModifiers(SimpleNode aNode, SimpleNode bNode, int[] modifierTypes) {
        List<Token> aMods = SimpleNodeUtil.getLeadingTokens(aNode);
        List<Token> bMods = SimpleNodeUtil.getLeadingTokens(bNode);

        Map<Integer, Token> aByKind = getModifierMap(aNode);
        Map<Integer, Token> bByKind = getModifierMap(bNode);
        
        for (int mi = 0; mi < modifierTypes.length; ++mi) {
            Integer modInt = Integer.valueOf(modifierTypes[mi]);
            Token   aMod   = aByKind.get(modInt);
            Token   bMod   = bByKind.get(modInt);

            if (aMod == null) {
                if (bMod != null) {
                    changed(aNode.getFirstToken(), bMod, MODIFIER_ADDED, bMod.image);
                }
            }
            else if (bMod == null) {
                changed(aMod, bNode.getFirstToken(), MODIFIER_REMOVED, aMod.image);
            }
        }
    }

    public void compareAccess(SimpleNode aNode, SimpleNode bNode) {
        Token aAccess = ItemUtil.getAccess(aNode);
        Token bAccess = ItemUtil.getAccess(bNode);

        if (aAccess == null) {
            if (bAccess != null) {
                changed(aNode.getFirstToken(), bAccess, ACCESS_ADDED, bAccess.image);
            }
        }
        else if (bAccess == null) {
            changed(aAccess, bNode.getFirstToken(), ACCESS_REMOVED, aAccess.image);
        }
        else if (!aAccess.image.equals(bAccess.image)) {
            changed(aAccess, bAccess, ACCESS_CHANGED, aAccess.image, bAccess.image);
        }
    }

    protected FileDiff replaceReference(String aName, FileDiff ref, Point aEndPt, Point bEndPt) {
        String   newMsg  = MessageFormat.format(CODE_CHANGED, aName);
        FileDiff newDiff = new FileDiffChange(newMsg, ref.getFirstStart(), aEndPt, ref.getSecondStart(), bEndPt);
        
        getFileDiffs().remove(ref);
        
        add(newDiff);
        
        return newDiff;
    }

    protected FileDiff addReference(String aName, String msg,
                                    Point aStPt, Point aEndPt,
                                    Point bStPt, Point bEndPt) {
        // String codeChgType = FileDiff.CHANGED;

        // // the change type is add if the new line is on its own line:

        // if (msg == CODE_ADDED && onEntireLine(b, addStart, addEnd, bStart, bEnd)) {
        //     codeChgType = FileDiff.ADDED;
        // }
        // else if (msg == CODE_REMOVED && onEntireLine(a, delStart, delEnd, aStart, aEnd)) {
        //     codeChgType = FileDiff.DELETED;
        // }

        // This assumes that a and b have the same name. Wouldn't they?
        String str = MessageFormat.format(msg, aName);

        FileDiff ref = null;

        if (msg == CODE_ADDED) {
            // this will show as add when highlighted, as change when not.
            ref = new FileDiffCodeAdded(str, aStPt, aEndPt, bStPt, bEndPt);
        }
        else if (msg == CODE_REMOVED) {
            ref = new FileDiffCodeDeleted(str, aStPt, aEndPt, bStPt, bEndPt);
        }
        else {
            ref = new FileDiffChange(str, aStPt, aEndPt, bStPt, bEndPt);
        }                    

        add(ref);

        return ref;
    }

    protected void compareCode(String aName, List<Token> a, String bName, List<Token> b) {
        Diff<Token> d = new Diff<Token>(a, b, new TokenComparator());
        
        FileDiff ref = null;
        List<Difference> diffList = d.diff();

        for (Difference diff : diffList) {
            int delStart = diff.getDeletedStart();
            int delEnd   = diff.getDeletedEnd();
            int addStart = diff.getAddedStart();
            int addEnd   = diff.getAddedEnd();

            tr.Ace.log("diff", diff);

            String msg    = null;
            Token  aStart = null;
            Token  aEnd   = null;
            Token  bStart = null;
            Token  bEnd   = null;

            if (delEnd == Difference.NONE) {
                if (addEnd == Difference.NONE) {
                    // WTF?
                    return;
                }
                else {
                    aStart = getStart(a, delStart);
                    aEnd   = aStart;
                    bStart = b.get(addStart);
                    bEnd   = b.get(addEnd);
                    msg    = CODE_ADDED;
                }
            }
            else if (addEnd == Difference.NONE) {
                aStart = a.get(delStart);
                aEnd   = a.get(delEnd);
                bStart = getStart(b, addStart);
                bEnd   = bStart;
                msg    = CODE_REMOVED;
            }
            else {
                aStart = a.get(delStart);
                aEnd   = a.get(delEnd);
                bStart = b.get(addStart);
                bEnd   = b.get(addEnd);
                msg    = CODE_CHANGED;
            }

            tr.Ace.log("msg", msg);
            
            Point aStPt  = FileDiff.toBeginPoint(aStart);
            Point aEndPt = FileDiff.toEndPoint(aEnd);
            Point bStPt  = FileDiff.toBeginPoint(bStart);
            Point bEndPt = FileDiff.toEndPoint(bEnd);

            tr.Ace.log("ref", ref);

            if (ref != null && ref.firstStart.x == aStPt.x) {
                ref = replaceReference(aName, ref, aEndPt, bEndPt);
            }
            else {
                ref = addReference(aName, msg, aStPt, aEndPt, bStPt, bEndPt);
            }
        }
    }

    protected boolean onEntireLine(List<Token> tokens, int tkIdxStart, int tkIdxEnd, Token startTk, Token endTk) {
        Token   prevToken = tkIdxStart   > 0             ? tokens.get(tkIdxStart - 1) : null;
        Token   nextToken = tkIdxEnd + 1 < tokens.size() ? tokens.get(tkIdxEnd   + 1) : null;
        
        boolean onEntLine = ((prevToken == null || prevToken.endLine   < startTk.beginLine) &&
                             (nextToken == null || nextToken.beginLine > endTk.endLine));        

        return onEntLine;
    }
    
    protected Token getStart(List<Token> list, int start) {
        Token stToken = ListExt.get(list, start);
        if (stToken == null && list.size() > 0) {
            stToken = ListExt.get(list, -1);
            stToken = stToken.next;
        }
        return stToken;
    }
}
