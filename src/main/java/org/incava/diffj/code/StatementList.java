package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.ListExt;
import org.incava.ijdk.util.diff.Difference;
import org.incava.pmdx.SimpleNodeUtil;

public class StatementList {
    private final List<Statement> statements;
    
    public StatementList(List<Statement> statements) {
        this.statements = statements;
    }

    public Statement get(int idx) {
        return ListExt.get(statements, idx);
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

    /**
     * Returns the range for the first token of the statement at the given
     * index.
     */
    public LocationRange getRangeAt(int idx) {
        Statement stmt = statements.get(idx);
        TokenList tokenList = stmt.getTokenList();
        return tokenList.getTokenLocationRange(0);
    }

    /**
     * Returns the range for the given statements within from and to, inclusive.
     */
    public LocationRange getRangeOf(int from, int to) {
        Statement fromStmt = get(from);
        Statement toStmt = get(to);
        Tkn startTkn = fromStmt.getTkn(0);
        Tkn endTkn = toStmt.getTkn(-1);
        return startTkn.getLocationRange(endTkn);
    }    
}
