package org.incava.diffj.params;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Differences;
import org.incava.diffj.Messages;

public class ParameterTypeMatch extends ParameterMatch {
    public ParameterTypeMatch(ASTFormalParameter fromFormalParam, int index, int typeMatch, int nameMatch, Parameters toParams) {
        super(fromFormalParam, index, typeMatch, nameMatch, toParams);
        tr.Ace.onWhite("index", index);
    }

    public void diff(Differences differences) {
        tr.Ace.onMagenta("differences", differences);

        int toIdx = getTypeMatch();
        Token fromNameTk = fromParam.getParameterName();
        Token toNameTk = toParams.getParameterName(toIdx);
        if (fromNameTk.image.equals(toNameTk.image)) {
            differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_REORDERED, fromNameTk.image, index, toIdx);
        }
        else {
            differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_REORDERED_AND_RENAMED, fromNameTk.image, index, toIdx, toNameTk.image);
        }
    }
}
