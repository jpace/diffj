package org.incava.diffj.code;

import java.util.List;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.SimpleNodeUtil;

public class Statement implements Comparable<Statement> {
    private final ASTBlockStatement stmt;
    private final List<Token> tokens;
    private final TokenList tokenList;

    public Statement(ASTBlockStatement stmt) {
        this.stmt = stmt;
        this.tokens = SimpleNodeUtil.getChildTokens(stmt);
        this.tokenList = new TokenList(tokens);
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public TokenList getTokenList() {
        return tokenList;
    }

    public ASTBlockStatement getBlockStatement() {
        return stmt;
    }

    public int compareTo(Statement other) {
        return tokenList.compareTo(other.tokenList);
    }

    public String toString() {
        return tokenList.toString();
    }

    public Tkn getTkn(int idx) {
        return new Tkn(tokenList.get(idx));
    }
}
