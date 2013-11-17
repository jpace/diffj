package org.incava.diffj.function;

import java.io.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.diffj.code.TokenList;
import org.incava.diffj.compunit.CompilationUnit;
import org.incava.diffj.io.JavaFile;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.util.diff.*;
import org.incava.java.Java;
import org.incava.pmdx.SimpleNodeUtil;
import static org.incava.diffj.code.Code.*;

public class TestMethodCodeByStatement extends ItemsTest {
    public TestMethodCodeByStatement(String name) {
        super(name);
    }

    public URL seek(String name) {
        return ClassLoader.getSystemResource(name);
    }

    public void dump(SimpleNode node) {
        SimpleNodeUtil.dump(node);
    }

    public TokenList dumpTokens(SimpleNode node) {
        TokenList tokens = new TokenList(node);
        tr.Ace.log("tokens", tokens);
        String str = tokens.toString();
        return tokens;
    }

    public SimpleNode getChildNode(SimpleNode parent, int idx) {
        return SimpleNodeUtil.findChild(parent, null, idx);
    }

    public CompilationUnit getCompilationUnit(String fileName) throws Exception {
        URL url = seek(fileName);
        File file = new File(url.toURI());
        JavaFile javaFile = new JavaFile(file, fileName, "1.5");
        return javaFile.compile();
    }

    /**
     * Returns the first method. Assumes type->class->body->method.
     */
    public SimpleNode getFirstMethod(ASTCompilationUnit ast) {
        SimpleNode typeDecl = getChildNode(ast, 0);
        SimpleNode clsDecl = getChildNode(typeDecl, 0);
        SimpleNode body = getChildNode(clsDecl, 0);

        SimpleNode bodyDecl = getChildNode(body, 0);
        return getChildNode(bodyDecl, 0);
    }

    public List<SimpleNode> getStatements(SimpleNode node) {
        return SimpleNodeUtil.findChildren(node);
    }

    public List<TokenList> show(String fileName) throws Exception {
        ASTCompilationUnit ast = getCompilationUnit(fileName).getAstCompUnit();
        SimpleNode meth = getFirstMethod(ast);

        SimpleNode methBlk = getChildNode(meth, 2);
        dump(methBlk);

        List<TokenList> tokenLists = new ArrayList<TokenList>();

        List<SimpleNode> statements = getStatements(methBlk);
        for (SimpleNode stmt : statements) {
            tr.Ace.yellow("stmt", stmt);
            dumpTokens(stmt);
            tokenLists.add(new TokenList(stmt));
        }
        return tokenLists;
    }

    public void testSomething() throws Exception {
        tr.Ace.setVerbose(true);
        tr.Ace.log("this", this);
        
        List<TokenList> a = show("diffj/codecomp/d0/Changed.java");
        List<TokenList> b = show("diffj/codecomp/d1/Changed.java");
        Diff<TokenList> diff = new Diff<TokenList>(a, b, new TokenList.TokenListComparator());
        List<Difference> diffs = diff.execute();
        tr.Ace.log("diffs", diffs);
    }
}
