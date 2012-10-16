package org.incava.diffj.params;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;

public class ParameterNoMatch extends ParameterMatch {
    public ParameterNoMatch(ASTFormalParameter fromFormalParam, int index, int typeMatch, int nameMatch, Parameters toParams) {
        super(fromFormalParam, index, typeMatch, nameMatch, toParams);
    }

    public void diff(Differences differences) {
        Token fromNameTk = getParameterName();
        differences.changed(fromFormalParam, toParams.getFormalParameters(), Parameters.PARAMETER_REMOVED, fromNameTk.image);
    }
}
