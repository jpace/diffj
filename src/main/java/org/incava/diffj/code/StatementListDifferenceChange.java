package org.incava.diffj.code;

import java.util.List;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.util.diff.Difference;

public class StatementListDifferenceChange extends StatementListDifference {
    public StatementListDifferenceChange(List<TokenList> fromTokenLists, List<TokenList> toTokenLists,
                                         Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(fromTokenLists, toTokenLists, delStart, delEnd, addStart, addEnd);
    }

    public void execute(String name, Differences differences) {
        TokenList fromList = getFromList();
        TokenList toList = getToList();

        Code fc = new Code(name, fromList);
        Code tc = new Code(name, toList);
        fc.diff(tc, differences);
    }
}
