package org.incava.diffj;

import java.text.MessageFormat;
import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffCodeAdded;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.analysis.FileDiffs;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.DefaultComparator;
import org.incava.ijdk.util.ListExt;
import org.incava.ijdk.util.diff.Diff;
import org.incava.ijdk.util.diff.Difference;
import org.incava.pmdx.*;

public class Code {    
    public static class TokenComparator extends DefaultComparator<Token> {
        public int doCompare(Token xt, Token yt) {
            int cmp = xt.kind < yt.kind ? -1 : (xt.kind > yt.kind ? 1 : 0);
            if (cmp == 0) {
                cmp = xt.image.compareTo(yt.image);
            }
            return cmp;
        }
    }

    private final String name;
    private final List<Token> tokens;

    public Code(String name, List<Token> tokens) {
        this.name = name;
        this.tokens = tokens;
    }

    public void diff(List<Token> toTokens, Differences differences) {
        Diff<Token> d = new Diff<Token>(tokens, toTokens, new TokenComparator());
        
        FileDiff fdiff = null;
        List<Difference> diffList = d.diff();

        for (Difference diff : diffList) {
            fdiff = processDifference(diff, toTokens, fdiff, differences);
            if (fdiff == null) {
                return;
            }
        }
    }

    protected FileDiff replaceReference(FileDiff fdiff, LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        String   newMsg  = MessageFormat.format(Messages.CODE_CHANGED, name);
        FileDiff newDiff = new FileDiffChange(newMsg, fdiff.getFirstLocation().getStart(), fromLocRg.getEnd(), fdiff.getSecondLocation().getStart(), toLocRg.getEnd());
        
        differences.getFileDiffs().remove(fdiff);
        differences.add(newDiff);

        return newDiff;
    }

    protected FileDiff addReference(String msg, LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        String str = MessageFormat.format(msg, name);

        FileDiff fdiff = null;

        if (msg.equals(Messages.CODE_ADDED)) {
            // this will show as add when highlighted, as change when not.
            fdiff = new FileDiffCodeAdded(str, fromLocRg, toLocRg);
        }
        else if (msg.equals(Messages.CODE_REMOVED)) {
            fdiff = new FileDiffCodeDeleted(str, fromLocRg, toLocRg);
        }
        else {
            fdiff = new FileDiffChange(str, fromLocRg, toLocRg);
        }                    

        differences.add(fdiff);

        return fdiff;
    }

    protected LocationRange getLocationRange(List<Token> tokenList, Integer start, Integer end) {
        Token startTk, endTk;
        if (end == Difference.NONE) {
            endTk = startTk = getStart(tokenList, start);
        }
        else {
            startTk = tokenList.get(start);
            endTk = tokenList.get(end);
        }
        return new LocationRange(FileDiff.toBeginLocation(startTk), FileDiff.toEndLocation(endTk));
    }
    
    protected boolean isOnSameLine(FileDiff fdiff, LocationRange loc) {
        return fdiff != null && fdiff.getFirstLocation().getStart().getLine() == loc.getStart().getLine();
    }

    protected FileDiff processDifference(Difference diff, List<Token> toTokens, FileDiff prevFdiff, Differences differences) {
        int delStart = diff.getDeletedStart();
        int delEnd   = diff.getDeletedEnd();
        int addStart = diff.getAddedStart();
        int addEnd   = diff.getAddedEnd();

        if (delEnd == Difference.NONE && addEnd == Difference.NONE) {
            // WTF?
            return null;
        }

        LocationRange fromLocRg = getLocationRange(tokens, delStart, delEnd);
        LocationRange toLocRg = getLocationRange(toTokens, addStart, addEnd);

        String msg = delEnd == Difference.NONE ? Messages.CODE_ADDED : (addEnd == Difference.NONE ? Messages.CODE_REMOVED : Messages.CODE_CHANGED);

        prevFdiff = isOnSameLine(prevFdiff, fromLocRg) ? replaceReference(prevFdiff, fromLocRg, toLocRg, differences) : addReference(msg, fromLocRg, toLocRg, differences);
        
        return prevFdiff;
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
