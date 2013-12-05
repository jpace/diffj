package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;
import org.incava.pmdx.SimpleNodeUtil;

public class Block {
    private final String name;
    private final List<Statement> statements;

    public Block(String name, SimpleNode blk) {
        this(name, SimpleNodeUtil.findChildren(blk, ASTBlockStatement.class));
    }

    public Block(String name, List<ASTBlockStatement> blkStatements) {
        this.name = name;
        
        tr.Ace.bold("blkStatements", blkStatements);
        this.statements = new ArrayList<Statement>();
        for (ASTBlockStatement blkStmt : blkStatements) {
            Statement stmt = new Statement(blkStmt);
            statements.add(stmt);
        }
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public TokenList getTokens() {
        tr.Ace.bold("statements", statements);
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
