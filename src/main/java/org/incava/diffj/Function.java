package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.pmdx.SimpleNodeUtil;

public class Function extends Items {
    public Function() {
    }

    protected void compareParameters(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams, Differences differences) {
        Parameters params = new Parameters(differences.getFileDiffs(), fromFormalParams, toFormalParams);
        params.compare();
    }
    
    protected void compareThrows(SimpleNode fromNode, ASTNameList fromNameList, SimpleNode toNode, ASTNameList toNameList, Differences differences) {
        Throws thrws = new Throws(fromNode, fromNameList);
        thrws.diff(toNode, toNameList, differences);
    }
}
