package org.incava.diffj.code;

import java.util.List;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public class TokenListDifferenceChange extends TokenListDifference {
    public TokenListDifferenceChange(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public void execute(String name,
                        List<TokenList> fromTokenLists, 
                        List<TokenList> toTokenLists, 
                        Differences differences) {
        tr.Ace.onBlue("this", this);
        TokenList fromList = getFromList(fromTokenLists);
        TokenList toList = getToList(toTokenLists);

        tr.Ace.log("fromList", fromList);
        tr.Ace.log("toList", toList);

        Code fc = new Code(name, fromList);
        tr.Ace.log("fc", fc);
        Code tc = new Code(name, toList);
        tr.Ace.log("tc", tc);

        fc.diff(tc, differences);
    }
}
