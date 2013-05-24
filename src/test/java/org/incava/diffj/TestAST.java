package org.incava.diffj;

import java.io.*;
import java.net.URL;
import java.util.List;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.io.JavaFile;
import org.incava.ijdk.text.Location;
import org.incava.java.Java;
import org.incava.pmdx.SimpleNodeUtil;

public class TestAST extends DiffJTest {
    public TestAST(String name) {
        super(name);
    }

    public URL seek(String name) {
        URL res = ClassLoader.getSystemResource(name);
        tr.Ace.log(name, res);
        return res;
    }

    public InputStream get(String name) {
        InputStream is = ClassLoader.getSystemResourceAsStream(name);
        tr.Ace.log(name, is);
        return is;
    }

    public void dump(SimpleNode node) {
        SimpleNodeUtil.dump(node);
    }

    public List<Token> dumpTokens(SimpleNode node) {
        List<Token> tokens = SimpleNodeUtil.getChildTokens(node);
        tr.Ace.log("tokens", tokens);
        return tokens;
    }

    public SimpleNode getChildNode(SimpleNode parent, int idx) {
        return SimpleNodeUtil.findChild(parent, null, idx);
    }

    public void show(String fileName) throws Exception {
        URL url = seek(fileName);
        File file = new File(url.toURI());

        JavaFile javaFile = new JavaFile(file, fileName, "1.5");
        
        ASTCompilationUnit ast = javaFile.compile();

        SimpleNode typeDecl = getChildNode(ast, 0);
        SimpleNode clsDecl = getChildNode(typeDecl, 0);
        SimpleNode body = getChildNode(clsDecl, 0);

        SimpleNode bodyDecl = getChildNode(body, 0);
        SimpleNode meth = getChildNode(bodyDecl, 0);

        SimpleNode methBlk = getChildNode(meth, 2);
        dump(methBlk);

        List<SimpleNode> statements = SimpleNodeUtil.findChildren(methBlk);
        for (SimpleNode stmt : statements) {
            tr.Ace.yellow("stmt", stmt);
            dumpTokens(stmt);
        }
    }

    public void testSomething() throws Exception {
        tr.Ace.setVerbose(true);
        tr.Ace.log("this", this);
        
        show("diffj/codecomp/d0/Changed.java");
        show("diffj/codecomp/d1/Changed.java");
    }
}
