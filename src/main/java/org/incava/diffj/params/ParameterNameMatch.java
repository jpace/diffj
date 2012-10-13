package org.incava.diffj.params;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Differences;
import org.incava.diffj.Messages;

public class ParameterNameMatch extends ParameterMatch {
    public ParameterNameMatch(ASTFormalParameter fromFormalParam, int index, int typeMatch, int nameMatch, Parameters toParams) {
        super(fromFormalParam, index, typeMatch, nameMatch, toParams);
        tr.Ace.onWhite("index", index);
    }

    public void diff(Differences differences) {
        tr.Ace.onMagenta("differences", differences);

        int toIdx = getNameMatch();
        Token fromNameTk = fromParam.getParameterName();
        ASTFormalParameter toParam = toParams.getParameter(toIdx);
        differences.changed(fromFormalParam, toParam, Messages.PARAMETER_REORDERED, fromNameTk.image, index, toIdx);
    }
}
