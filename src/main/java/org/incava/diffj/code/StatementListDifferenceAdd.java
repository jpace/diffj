package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeAdded;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;

public class StatementListDifferenceAdd extends StatementListDifference {
    public StatementListDifferenceAdd(StatementList fromStatements, StatementList toStatements,
                                      Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(fromStatements, toStatements, delStart, delEnd, addStart, addEnd);
    }

    public String getMessage(String name) {
        return Code.CODE_ADDED.format(name);
    }

    public void process(String name, TokenList fromList, TokenList toList, Differences differences) {
        tr.Ace.onBlue("this", this);
        
        LocationRange flr = fromList.getTokenLocationRange(0);
        tr.Ace.cyan("flr", flr);
        
        LocationRange tlr = toList.getFullLocationRange();        
        tr.Ace.cyan("tlr", tlr);

        String msg = getMessage(name);        
        FileDiff fileDiff = new FileDiffCodeAdded(msg, flr, tlr);
        differences.add(fileDiff);
    }
}
