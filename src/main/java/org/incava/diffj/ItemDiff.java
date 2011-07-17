package org.incava.diffj;

import java.awt.Point;
import java.text.MessageFormat;
import java.util.*;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.*;
import org.incava.ijdk.util.*;
import org.incava.ijdk.util.diff.*;
import org.incava.pmd.*;
import org.incava.qualog.Qualog;


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
                if (bMod == null) {
                    // no change
                }
                else {
                    changed(aNode.getFirstToken(), bMod, MODIFIER_ADDED, bMod.image);
                }
            }
            else if (bMod == null) {
                changed(aMod, bNode.getFirstToken(), MODIFIER_REMOVED, aMod.image);
            }
            else if (aMod.kind == bMod.kind) {
                // no change (in modifier type)
            }
            else {
                // huh? we don't hit this, evidently.

                tr.Ace.red("aMod", aMod);
                tr.Ace.red("bMod", bMod);
                changed(aMod, bMod, MODIFIER_ADDED);
            }
        }
    }

    public void compareAccess(SimpleNode aNode, SimpleNode bNode) {
        Token aAccess = ItemUtil.getAccess(aNode);
        Token bAccess = ItemUtil.getAccess(bNode);

        if (aAccess == null) {
            if (bAccess == null) {
                // no access, no change
            }
            else {
                changed(aNode.getFirstToken(), bAccess, ACCESS_ADDED, bAccess.image);
            }
        }
        else if (bAccess == null) {
            changed(aAccess, bNode.getFirstToken(), ACCESS_REMOVED, aAccess.image);
        }
        else if (aAccess.image.equals(bAccess.image)) {
            // no access change
        }
        else {
            changed(aAccess, bAccess, ACCESS_CHANGED, aAccess.image, bAccess.image);
        }
    }

    protected void compareCode(String aName, List<Token> a, String bName, List<Token> b) {
        Diff<Token> d = new Diff<Token>(a, b, new DefaultComparator<Token>() {
                public int doCompare(Token xt, Token yt) {
                    int cmp = xt.kind < yt.kind ? -1 : (xt.kind > yt.kind ? 1 : 0);
                    if (cmp == 0) {
                        cmp = xt.image.compareTo(yt.image);
                    }
                    return cmp;
                }
            });

        FileDiff ref = null;
        List<Difference> diffList = d.diff();

        Token lastA = null;
        Token lastB = null;

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
                // replace reference ...

                String   newMsg  = MessageFormat.format(CODE_CHANGED, aName);
                FileDiff newDiff = new FileDiffChange(newMsg, ref.getFirstStart(), aEndPt, ref.getSecondStart(), bEndPt);

                getFileDiffs().remove(ref);

                add(newDiff);

                ref = newDiff;
            }
            else {
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
        Token stToken = start >= list.size() ? null : list.get(start);
        if (stToken == null && list.size() > 0) {
            stToken = list.get(list.size() - 1);
            stToken = stToken.next;
        }
        return stToken;
    }
}
