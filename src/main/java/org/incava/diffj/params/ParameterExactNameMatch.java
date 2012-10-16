package org.incava.diffj.params;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import org.incava.diffj.element.Differences;
import org.incava.diffj.Messages;

public class ParameterExactNameMatch extends ParameterMatch {
    public ParameterExactNameMatch(ASTFormalParameter fromFormalParam, int index, int typeMatch, int nameMatch, Parameters toParams) {
        super(fromFormalParam, index, typeMatch, nameMatch, toParams);
        tr.Ace.onWhite("index", index);
    }

    public void diff(Differences differences) {
        tr.Ace.onMagenta("differences", differences);

        ASTFormalParameter toFormalParam = toParams.getParameter(index);
        Parameter toParam = new Parameter(toFormalParam);
        String fromType = fromParam.getParameterType();
        String toType = toParam.getParameterType();
        differences.changed(fromFormalParam, toFormalParam, Messages.PARAMETER_TYPE_CHANGED, fromType, toType);
    }
}
