package org.incava.diffj.code;

import org.incava.diffj.element.Differences;

public class StatementListDifferenceChange extends StatementListDifference {
    public StatementListDifferenceChange(StatementList fromStatements, StatementList toStatements,
                                         Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(fromStatements, toStatements, delStart, delEnd, addStart, addEnd);
    }

    public void process(String name, TokenList fromList, TokenList toList, Differences differences) {
        Code fc = new Code(name, fromList);
        Code tc = new Code(name, toList);
        fc.diff(tc, differences);
    }
}
