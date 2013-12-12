package org.incava.diffj.code;

import java.util.List;
import org.incava.analysis.FileDiff;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public abstract class TokenListDifference extends Difference {
    public TokenListDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public TokenList getAsTokenList(List<TokenList> tokenLists, int from, int to) {
        int idx = from;
        TokenList list = tokenLists.get(idx++);
        while (idx <= to) {
            list.add(tokenLists.get(idx++));
        }

        return list;
    }

    public abstract void execute(String name, List<TokenList> fromTokenLists, List<TokenList> toTokenLists, Differences differences);

    public TokenList getFromList(List<TokenList> fromTokenLists) {
        tr.Ace.onRed("this", this);
        TokenList fromList = getAsTokenList(fromTokenLists, getDeletedStart(), getDeletedEnd());
        tr.Ace.log("fromList", fromList);
        return fromList;
    }

    public TokenList getToList(List<TokenList> toTokenLists) {
        tr.Ace.onRed("this", this);
        TokenList toList = getAsTokenList(toTokenLists, getAddedStart(), getAddedEnd());
        tr.Ace.log("toList", toList);
        return toList;
    }
}
