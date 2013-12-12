package org.incava.diffj.code;

import java.util.List;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeAdded;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public class TokenListDifferenceAdd extends TokenListDifference {
    public TokenListDifferenceAdd(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public String getMessage(String name) {
        return Code.CODE_ADDED.format(name);
    }

    public void execute(String name,
                        List<TokenList> fromTokenLists, 
                        List<TokenList> toTokenLists, 
                        Differences differences) {
        tr.Ace.onBlue("this", this);
        
        TokenList fromList = getFromList(fromTokenLists);
        LocationRange flr = fromList.getLocationRange(0, Difference.NONE);
        tr.Ace.cyan("flr", flr);
        
        TokenList toList = getToList(toTokenLists);
        LocationRange tlr = toList.getAsLocationRange();        
        tr.Ace.cyan("tlr", tlr);

        tr.Ace.log("fromList", fromList);
        tr.Ace.log("toList", toList);

        String msg = getMessage(name);        
        FileDiff fileDiff = new FileDiffCodeAdded(msg, flr, tlr);
        differences.add(fileDiff);
    }
}
