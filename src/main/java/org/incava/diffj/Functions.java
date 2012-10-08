package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.SimpleNodeUtil;

public class Functions extends Items {
    public Functions(FileDiffs differences) {
        super(differences);
    }

    protected void compareReturnTypes(SimpleNode fromNode, SimpleNode toNode) {
        SimpleNode fromRetType    = SimpleNodeUtil.findChild(fromNode);
        SimpleNode toRetType      = SimpleNodeUtil.findChild(toNode);
        String     fromRetTypeStr = SimpleNodeUtil.toString(fromRetType);
        String     toRetTypeStr   = SimpleNodeUtil.toString(toRetType);

        if (!fromRetTypeStr.equals(toRetTypeStr)) {
            differences.changed(fromRetType, toRetType, Messages.RETURN_TYPE_CHANGED, fromRetTypeStr, toRetTypeStr);
        }
    }

    protected void compareParameters(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams) {
        Parameters params = new Parameters(differences.getFileDiffs(), fromFormalParams, toFormalParams);
        params.compare();
    }
    
    protected void compareThrows(SimpleNode fromNode, ASTNameList fromNameList, SimpleNode toNode, ASTNameList toNameList) {
        Throws thrws = new Throws(differences.getFileDiffs());
        thrws.compareThrows(fromNode, fromNameList, toNode, toNameList);
    }
}
