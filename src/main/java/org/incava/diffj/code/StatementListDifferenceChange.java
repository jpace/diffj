package org.incava.diffj.code;

import java.util.List;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.util.diff.Difference;

public class StatementListDifferenceChange extends StatementListDifference {
    public StatementListDifferenceChange(List<Statement> fromStatements, List<Statement> toStatements,
                                         Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(fromStatements, toStatements, delStart, delEnd, addStart, addEnd);
    }

    public void process(String name, TokenList fromList, TokenList toList, Differences differences) {
        Code fc = new Code(name, fromList);
        Code tc = new Code(name, toList);
        fc.diff(tc, differences);
    }
}
