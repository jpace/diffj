package org.incava.diffj.params;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Differences;
import org.incava.diffj.Messages;

/**
 * The type matches for the given index.
 */
public class ParameterExactTypeMatch extends ParameterMatch {
    public ParameterExactTypeMatch(ASTFormalParameter fromFormalParam, int index, int typeMatch, int nameMatch, Parameters toParams) {
        super(fromFormalParam, index, typeMatch, nameMatch, toParams);
        tr.Ace.onWhite("index", index);
    }

    public void diff(Differences differences) {
        tr.Ace.onMagenta("differences", differences);

        Token fromNameTk = getParameterName();
        Token toNameTk = toParams.getParameterName(index);
        differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_NAME_CHANGED, fromNameTk.image, toNameTk.image);
    }
}
