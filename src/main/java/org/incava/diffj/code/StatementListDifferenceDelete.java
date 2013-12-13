package org.incava.diffj.code;

import java.util.List;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;

public class StatementListDifferenceDelete extends StatementListDifference {
    public StatementListDifferenceDelete(List<TokenList> fromTokenLists, List<TokenList> toTokenLists,
                                         Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(fromTokenLists, toTokenLists, delStart, delEnd, addStart, addEnd);
    }

    public String getMessage(String name) {
        return Code.CODE_REMOVED.format(name);
    }

    public void process(String name, TokenList fromList, TokenList toList, Differences differences) {
        tr.Ace.onBlue("this", this);

        LocationRange flr = fromList.getFullLocationRange();
        tr.Ace.cyan("flr", flr);

        LocationRange tlr = toList.getTokenLocationRange(0);
        tr.Ace.cyan("tlr", tlr);

        String msg = getMessage(name);
        FileDiff fileDiff = new FileDiffCodeDeleted(msg, flr, tlr);
        differences.add(fileDiff);
    }
}
