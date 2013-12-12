package org.incava.diffj.code;

import java.util.List;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public class StatementListDifferenceDelete extends StatementListDifference {
    public StatementListDifferenceDelete(List<TokenList> fromTokenLists, List<TokenList> toTokenLists,
                                         Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(fromTokenLists, toTokenLists, delStart, delEnd, addStart, addEnd);
    }

    public String getMessage(String name) {
        return Code.CODE_REMOVED.format(name);
    }

    public void execute(String name, Differences differences) {
        tr.Ace.onBlue("this", this);

        TokenList fromList = getFromList();
        LocationRange flr = fromList.getAsLocationRange();
        tr.Ace.cyan("flr", flr);

        TokenList toList = getToList();
        LocationRange tlr = toList.getLocationRange(0, Difference.NONE);
        tr.Ace.cyan("tlr", tlr);

        String msg = getMessage(name);
        FileDiff fileDiff = new FileDiffCodeDeleted(msg, flr, tlr);
        differences.add(fileDiff);
    }
}
