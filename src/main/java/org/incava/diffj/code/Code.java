package org.incava.diffj.code;

import java.util.List;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;
import org.incava.ijdk.util.diff.Differ;

public class Code {    
    public static final Message CODE_CHANGED = new Message("code changed in {0}");
    public static final Message CODE_ADDED = new Message("code added in {0}");
    public static final Message CODE_REMOVED = new Message("code removed in {0}");

    private final String name;
    private final TokenList tokenList;

    public Code(String name, List<Token> tokens) {
        this.name = name;
        this.tokenList = new TokenList(tokens);
    }

    public Code(String name, TokenList tokens) {
        this.name = name;
        this.tokenList = tokens;
    }

    public void diff(Code toCode, Differences differences) {
        TokenList toTokenList = toCode.tokenList;
        FileDiff currFileDiff = null;
        List<TokenDifference> diffList = tokenList.diff(toTokenList);

        for (TokenDifference diff : diffList) {
            currFileDiff = processDifference(diff, toTokenList, currFileDiff, differences);
            if (currFileDiff == null) {
                break;
            }
        }
    }

    protected FileDiff replaceReference(FileDiff fileDiff, LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        String newMsg = CODE_CHANGED.format(name);
        FileDiff newDiff = new FileDiffChange(newMsg, fileDiff, fromLocRg, toLocRg);
        differences.getFileDiffs().remove(fileDiff);
        differences.add(newDiff);
        return newDiff;
    }
    
    protected FileDiff processDifference(TokenDifference diff, TokenList toTokenList, FileDiff currFileDiff, Differences differences) {
        LocationRange fromLocRg = diff.getDeletedRange(tokenList);
        LocationRange toLocRg = diff.getAddedRange(toTokenList);

        if (currFileDiff != null && currFileDiff.isOnSameLine(fromLocRg)) {
            return replaceReference(currFileDiff, fromLocRg, toLocRg, differences);
        }
        else {
            return diff.execute(name, fromLocRg, toLocRg, differences);
        }
    }
}
