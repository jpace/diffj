package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeAdded;
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
        for (Statement stmt : statements) {
            tokenLists.add(new TokenList(stmt.getTokens()));
        }
        return tokenLists;
    }

    public void compareCode(Block toBlock, Differences differences) {
        Code fromCode = new Code(name, getTokens());
        Code toCode = new Code(name, toBlock.getTokens());
        fromCode.diff(toCode, differences);
    }

    public void processAddedStatements(List<TokenList> fromTokenLists, 
                                       List<TokenList> toTokenLists, 
                                       Difference df, Differences differences) {
        tr.Ace.onRed("df", df);
        int from = df.getDeletedStart();
        int to = df.getAddedStart();
        tr.Ace.log("from", from);
        tr.Ace.log("to", to);

        TokenList alist = fromTokenLists.get(df.getDeletedStart());
        tr.Ace.log("alist", alist);
        ++from;

        while (from <= df.getDeletedEnd()) {
            alist.add(fromTokenLists.get(from));
            ++from;
        }
        TokenList blist = toTokenLists.get(df.getAddedStart());
        tr.Ace.log("blist", blist);
        ++to;
        while (to <= df.getAddedEnd()) {
            blist.add(toTokenLists.get(to));
            ++to;
        }

        tr.Ace.log("alist", alist);
        tr.Ace.log("blist", blist);

        LocationRange fromLocRg = alist.getLocationRange(df.getDeletedStart(), df.getDeletedEnd());
        tr.Ace.log("fromLocRg", fromLocRg);
        LocationRange toLocRg = blist.getLocationRange(df.getAddedStart(), df.getAddedEnd());
        tr.Ace.log("toLocRg", toLocRg);

        String str = Code.CODE_ADDED.format(name);        
        FileDiff fileDiff = new FileDiffCodeAdded(str, fromLocRg, toLocRg);
        differences.add(fileDiff);
    }

    public void processChangedStatements(List<TokenList> fromTokenLists,
                                         List<TokenList> toTokenLists, 
                                         Difference df, Differences differences) {
        int from = df.getDeletedStart();
        int to = df.getAddedStart();
        tr.Ace.log("from", from);
        tr.Ace.log("to", to);
        TokenList alist = fromTokenLists.get(df.getDeletedStart());
        tr.Ace.log("alist", alist);
        ++from;

        while (from <= df.getDeletedEnd()) {
            alist.add(fromTokenLists.get(from));
            ++from;
        }
        TokenList blist = toTokenLists.get(df.getAddedStart());
        tr.Ace.log("blist", blist);
        ++to;
        while (to <= df.getAddedEnd()) {
            blist.add(toTokenLists.get(to));
            ++to;
        }

        tr.Ace.log("alist", alist);
        tr.Ace.log("blist", blist);

        Code fc = new Code(name, alist);
        tr.Ace.log("fc", fc);
        Code tc = new Code(name, blist);
        tr.Ace.log("tc", tc);

        fc.diff(tc, differences);
    }
    
    public void compareCodeNew(Block toBlock, Differences differences) {
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
                processAddedStatements(fromTokenLists, toTokenLists, df, differences);
            }
            else if (df.isChange()) {
                processChangedStatements(fromTokenLists, toTokenLists, df, differences);
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
