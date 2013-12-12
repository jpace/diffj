package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeAdded;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.*;
import org.incava.pmdx.SimpleNodeUtil;

public class Block {
    private final String name;
    private final List<Statement> statements;

    public Block(String name, SimpleNode blk) {
        this(name, SimpleNodeUtil.findChildren(blk, ASTBlockStatement.class));
    }

    public Block(String name, List<ASTBlockStatement> blkStatements) {
        this.name = name;        
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
        List<Token> allTokens = new ArrayList<Token>();
        for (Statement stmt : statements) {
            allTokens.addAll(stmt.getTokens());
        }
        return new TokenList(allTokens);
    }

    public List<TokenList> getTokenLists() {
        List<TokenList> tokenLists = new ArrayList<TokenList>();
        for (Statement stmt : getStatements()) {
            tokenLists.add(new TokenList(stmt.getTokens()));
        }
        return tokenLists;
    }

    public void compareCode(Block toBlock, Differences differences) {
        compareCodeNew(toBlock, differences);
    }

    public void compareCodeOld(Block toBlock, Differences differences) {
        Code fromCode = new Code(name, getTokens());
        Code toCode = new Code(name, toBlock.getTokens());
        fromCode.diff(toCode, differences);
    }
    
    public void compareCodeNew(Block toBlock, Differences differences) {
        List<TokenList> fromTokenLists = getTokenLists();
        List<TokenList> toTokenLists = toBlock.getTokenLists();
        tr.Ace.cyan("fromTokenLists", fromTokenLists);
        tr.Ace.yellow("toTokenLists", toTokenLists);

        StatementListDiffer diff = new StatementListDiffer(fromTokenLists, toTokenLists);
        List<StatementListDifference> diffs = diff.execute();
        tr.Ace.log("diffs", diffs);
        for (StatementListDifference df : diffs) {
            tr.Ace.yellow("df", df);
            df.execute(name, differences);
        }
    }
}
