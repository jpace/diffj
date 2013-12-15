package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Differ;
import org.incava.ijdk.util.diff.Difference;
import org.incava.pmdx.SimpleNodeUtil;

public class StatementList {
    private final List<Statement> statements;
    
    public StatementList(List<Statement> statements) {
        this.statements = statements;
    }

    public List<TokenList> getTokenLists() {
        List<TokenList> tokenLists = new ArrayList<TokenList>();
        for (Statement stmt : statements) {
            tokenLists.add(stmt.getTokenList());
        }
        return tokenLists;
    }

    public TokenList getAsTokenList(Integer from, Integer to) {
        List<TokenList> tokenLists = getTokenLists();
        if (to == Difference.NONE) {
            return tokenLists.get(from);
        }
        
        int idx = from;
        TokenList list = tokenLists.get(idx++);
        while (idx <= to) {
            list.add(tokenLists.get(idx++));
        }

        return list;
    }
}
