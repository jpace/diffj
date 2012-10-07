package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.SimpleNodeUtil;

public class FunctionDiff extends ItemDiff {
    public FunctionDiff(FileDiffs differences) {
        super(differences);
    }

    protected void compareReturnTypes(SimpleNode fromNode, SimpleNode toNode) {
        SimpleNode fromRetType    = SimpleNodeUtil.findChild(fromNode);
        SimpleNode toRetType      = SimpleNodeUtil.findChild(toNode);
        String     fromRetTypeStr = SimpleNodeUtil.toString(fromRetType);
        String     toRetTypeStr   = SimpleNodeUtil.toString(toRetType);

        if (!fromRetTypeStr.equals(toRetTypeStr)) {
            changed(fromRetType, toRetType, Messages.RETURN_TYPE_CHANGED, fromRetTypeStr, toRetTypeStr);
        }
    }

    protected void compareParameters(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams) {
        ParameterDiff pd = new ParameterDiff(getFileDiffs());
        pd.compareParameters(fromFormalParams, toFormalParams);
    }
    
    protected void compareThrows(SimpleNode fromNode, ASTNameList fromNameList, SimpleNode toNode, ASTNameList toNameList) {
        ThrowsDiff td = new ThrowsDiff(getFileDiffs());
        td.compareThrows(fromNode, fromNameList, toNode, toNameList);
    }
}
