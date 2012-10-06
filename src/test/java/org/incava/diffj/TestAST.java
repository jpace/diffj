package org.incava.diffj;

import java.io.*;
import java.net.URL;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;
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

    public SimpleNode getChild(SimpleNode parent, int idx, boolean show) {
        SimpleNode child = SimpleNodeUtil.findChild(parent, (String)null, idx);
        if (show) {
            dump(child);
        }
        return child;
    }

    public void show(String fileName) throws Exception {
        URL url = seek(fileName);
        File file = new File(url.toURI());

        JavaFile javaFile = new JavaFile(file, fileName, "1.5");
        
        ASTCompilationUnit ast = javaFile.compile();

        SimpleNode typeDecl = getChild(ast, 0, false);
        SimpleNode clsDecl = getChild(typeDecl, 0, false);
        SimpleNode body = getChild(clsDecl, 0, false);

        SimpleNode bodyDecl = getChild(body, 0, false);
        SimpleNode meth = getChild(bodyDecl, 0, false);

        SimpleNode methBlk = getChild(meth, 2, true);
    }

    public void testSomething() throws Exception {
        tr.Ace.setVerbose(true);
        tr.Ace.log("this", this);
        
        show("diffj/codecomp/d0/Changed.java");
        show("diffj/codecomp/d1/Changed.java");
    }
}
