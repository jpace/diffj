package org.incava.diffj.code.statements;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.code.Block;
import org.incava.diffj.code.Statement;
import org.incava.diffj.code.Tkn;
import org.incava.diffj.code.TokenList;
import org.incava.diffj.util.DiffPoint;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.ListExt;
import org.incava.ijdk.util.diff.Difference;
import org.incava.pmdx.SimpleNodeUtil;
import static org.incava.ijdk.util.IUtil.*;

public class StatementList {
    private final List<Statement> statements;
    private final Block blk;
    
    public StatementList(Block blk) {
        this.blk = blk;
        this.statements = blk.getStatements();
        SLLogger.log("statements", statements);
    }

    public Block getBlock() {
        return blk;
    }

    public String toString() {
        return statements.toString();
    }

    public Statement get(int idx) {
        SLLogger.log("idx", idx);
        return ListExt.get(statements, idx);
    }

    public List<TokenList> getTokenLists() {
        List<TokenList> tokenLists = new ArrayList<TokenList>();
        for (Statement stmt : statements) {
            tokenLists.add(stmt.getTokenList());
        }
        return tokenLists;
    }

    public TokenList getAsTokenList(DiffPoint diffPoint) {
        return getAsTokenList(diffPoint.getStart(), diffPoint.getEnd());
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
        SLLogger.log("idx", idx);
        Statement stmt = get(idx);
        SLLogger.log("stmt", stmt);
        if (stmt == null) {
            Token token = blk.getLastToken();
            List<Token> tokens = list(token);
            TokenList tokenList = new TokenList(tokens);
            LocationRange rg = tokenList.getTokenLocationRange(-1);
            return rg;
        }
        TokenList tokenList = stmt.getTokenList();
        SLLogger.log("tokenList", tokenList);
        return tokenList.getTokenLocationRange(0);
    }

    /**
     * Returns the range for the given statements for the diff point, inclusive.
     */
    public LocationRange getRangeOf(DiffPoint diffPoint) {
        int from = diffPoint.getStart();
        int to = diffPoint.getEnd();
        Statement fromStmt = get(from);
        Statement toStmt = get(to);
        Tkn startTkn = fromStmt.getTkn(0);
        Tkn endTkn = toStmt.getTkn(-1);
        return startTkn.getLocationRange(endTkn);
    }
}
