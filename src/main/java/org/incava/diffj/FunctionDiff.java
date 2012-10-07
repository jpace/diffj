package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.SimpleNodeUtil;

public class FunctionDiff extends ItemDiff {
    public static final String RETURN_TYPE_CHANGED = "return type changed from {0} to {1}";

    public static final String PARAMETER_REMOVED = "parameter removed: {0}";
    public static final String PARAMETER_ADDED = "parameter added: {0}";
    public static final String PARAMETER_REORDERED = "parameter {0} reordered from argument {1} to {2}";
    public static final String PARAMETER_TYPE_CHANGED = "parameter type changed from {0} to {1}";
    public static final String PARAMETER_NAME_CHANGED = "parameter name changed from {0} to {1}";
    public static final String PARAMETER_REORDERED_AND_RENAMED = "parameter {0} reordered from argument {1} to {2} and renamed {3}";

    public static final String THROWS_REMOVED = "throws removed: {0}";
    public static final String THROWS_ADDED = "throws added: {0}";

    public static final String THROWS_REORDERED = "throws {0} reordered from argument {1} to {2}";

    public FunctionDiff(FileDiffs differences) {
        super(differences);
    }

    protected void compareReturnTypes(SimpleNode fromNode, SimpleNode toNode) {
        SimpleNode fromRetType    = SimpleNodeUtil.findChild(fromNode);
        SimpleNode toRetType      = SimpleNodeUtil.findChild(toNode);
        String     fromRetTypeStr = SimpleNodeUtil.toString(fromRetType);
        String     toRetTypeStr   = SimpleNodeUtil.toString(toRetType);

        if (!fromRetTypeStr.equals(toRetTypeStr)) {
            changed(fromRetType, toRetType, RETURN_TYPE_CHANGED, fromRetTypeStr, toRetTypeStr);
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
