package org.incava.diffj.params;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import org.incava.diffj.element.Differences;

public class ParameterExactNameMatch extends ParameterMatch {
    public ParameterExactNameMatch(ASTFormalParameter fromFormalParam, int index, int typeMatch, int nameMatch, Parameters toParams) {
        super(fromFormalParam, index, typeMatch, nameMatch, toParams);
    }

    public void diff(Differences differences) {
        ASTFormalParameter toFormalParam = toParams.getParameter(index);
        Parameter toParam = new Parameter(toFormalParam);
        String fromType = fromParam.getParameterType();
        String toType = toParam.getParameterType();
        differences.changed(fromFormalParam, toFormalParam, Parameters.PARAMETER_TYPE_CHANGED, fromType, toType);
    }
}
