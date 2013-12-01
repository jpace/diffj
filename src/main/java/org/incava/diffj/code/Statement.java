package org.incava.diffj.code;

import java.util.List;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.SimpleNodeUtil;

public class Statement {
    private final ASTBlockStatement stmt;
    private final List<Token> tokens;

    public Statement(ASTBlockStatement stmt) {
        this.stmt = stmt;
        this.tokens = SimpleNodeUtil.getChildTokens(stmt);
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public ASTBlockStatement getBlockStatement() {
        return stmt;
    }
}
