package org.incava.diffj.params;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;

public class ParameterNameMatch extends ParameterMatch {
    public ParameterNameMatch(ASTFormalParameter fromFormalParam, int index, int typeMatch, int nameMatch, Parameters toParams) {
        super(fromFormalParam, index, typeMatch, nameMatch, toParams);
    }

    public void diff(Differences differences) {
        int toIdx = getNameMatch();
        Token fromNameTk = fromParam.getParameterName();
        ASTFormalParameter toParam = toParams.getParameter(toIdx);
        differences.changed(fromFormalParam, toParam, Parameters.PARAMETER_REORDERED, fromNameTk.image, index, toIdx);
    }
}
