package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;
import org.incava.pmdx.SimpleNodeUtil;

public class Block {
    private final String name;
    private final ASTBlock blk;
    private final List<Statement> statements;

    public Block(String name, ASTBlock blk) {
        this.name = name;
        this.blk = blk;
        List<ASTBlockStatement> stmts = SimpleNodeUtil.findChildren(blk, ASTBlockStatement.class);
        
        tr.Ace.bold("stmts", stmts);
        this.statements = new ArrayList<Statement>();
        for (ASTBlockStatement blkStmt : stmts) {
            Statement stmt = new Statement(blkStmt);
            statements.add(stmt);
        }
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public TokenList getTokens() {
        tr.Ace.bold("statements", statements);
        // this.tokens = new TokenList(blk);
        List<Token> allTokens = new java.util.ArrayList<Token>();
        for (Statement stmt : statements) {
            allTokens.addAll(stmt.getTokens());
        }
        TokenList tokens = new TokenList(allTokens);
        tr.Ace.bold("tokens", tokens);
        return tokens;
    }

    public void compareCode(Block toBlock, Differences differences) {
        Code fromCode = new Code(name, getTokens());
        Code toCode = new Code(name, toBlock.getTokens());
        fromCode.diff(toCode, differences);
    }
}
