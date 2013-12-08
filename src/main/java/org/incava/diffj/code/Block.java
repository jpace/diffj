package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;
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
        List<Token> allTokens = new ArrayList<Token>();
        for (Statement stmt : statements) {
            allTokens.addAll(stmt.getTokens());
        }
        TokenList tokens = new TokenList(allTokens);
        tr.Ace.bold("tokens", tokens);
        return tokens;
    }

    public List<TokenList> getTokenLists() {
        tr.Ace.bold("statements", statements);
        List<TokenList> tokenLists = new ArrayList<TokenList>();
        for (Statement stmt : statements) {
            tokenLists.add(new TokenList(stmt.getTokens()));
        }
        tr.Ace.log("tokenLists", tokenLists);
        return tokenLists;
    }

    public void compareCode(Block toBlock, Differences differences) {
        Code fromCode = new Code(name, getTokens());
        Code toCode = new Code(name, toBlock.getTokens());
        fromCode.diff(toCode, differences);
    }

    public void compareCodeNew(Block toBlock, Differences differences) {
        Code fromCode = new Code(name, getTokens());
        tr.Ace.log("fromCode", fromCode);
        Code toCode = new Code(name, toBlock.getTokens());
        tr.Ace.log("toCode", toCode);

        List<TokenList> fromTokenLists = getTokenLists();
        List<TokenList> toTokenLists = toBlock.getTokenLists();
        tr.Ace.cyan("fromTokenLists", fromTokenLists);
        tr.Ace.yellow("toTokenLists", toTokenLists);

        Diff<TokenList> diff = new Diff<TokenList>(fromTokenLists, toTokenLists);
        List<Difference> diffs = diff.execute();
        tr.Ace.log("diffs", diffs);
        for (Difference df : diffs) {
            tr.Ace.yellow("df", df);
            tr.Ace.yellow("df.add?", df.isAdd());
            tr.Ace.yellow("df.change?", df.isChange());
            tr.Ace.yellow("df.delete?", df.isDelete());
            if (df.isAdd()) {
                tr.Ace.onRed("df", df);
            }
            else if (df.isChange()) {
                int from = df.getDeletedStart();
                int to = df.getAddedEnd();
                tr.Ace.log("from", from);
                tr.Ace.log("to", to);
                TokenList alist = fromTokenLists.get(df.getDeletedStart());
                tr.Ace.log("alist", alist);
                
                TokenList blist = toTokenLists.get(df.getAddedEnd());
                tr.Ace.log("blist", blist);

                Code fc = new Code(name, alist);
                tr.Ace.log("fc", fc);
                Code tc = new Code(name, blist);
                tr.Ace.log("tc", tc);

                fc.diff(tc, differences);
            }
            else if (df.isDelete()) {
                tr.Ace.onRed("df", df);
                TokenDifferenceDelete tdd = new TokenDifferenceDelete(df.getDeletedStart(), df.getDeletedEnd(), 
                                                                      df.getAddedStart(), df.getAddedEnd());
                tr.Ace.red("tdd", tdd);
            }
        }

        // fromCode.diff(toCode, differences);
    }
}
