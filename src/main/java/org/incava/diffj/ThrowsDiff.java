package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.ThrowsUtil;

public class ThrowsDiff extends DiffComparator {
    public static final String THROWS_REMOVED = "throws removed: {0}";
    public static final String THROWS_ADDED = "throws added: {0}";
    public static final String THROWS_REORDERED = "throws {0} reordered from argument {1} to {2}";

    public ThrowsDiff(FileDiffs differences) {
        super(differences);
    }
    
    public void compareThrows(SimpleNode fromNode, ASTNameList fromNameList, SimpleNode toNode, ASTNameList toNameList) {
        if (fromNameList == null) {
            if (toNameList != null) {
                addAllThrows(fromNode, toNameList);
            }
        }
        else if (toNameList == null) {
            removeAllThrows(fromNameList, toNode);
        }
        else {
            compareEachThrow(fromNameList, toNameList);
        }
    }

    public List<ASTName> getChildNames(ASTNameList nameList) {
        return SimpleNodeUtil.snatchChildren(nameList, "net.sourceforge.pmd.ast.ASTName");
    }

    protected void changeThrows(SimpleNode fromNode, SimpleNode toNode, String msg, ASTName name) {
        changed(fromNode, toNode, msg, SimpleNodeUtil.toString(name));
    }

    protected void addAllThrows(SimpleNode fromNode, ASTNameList toNameList) {
        List<ASTName> names = getChildNames(toNameList);
        for (ASTName name : names) {
            changeThrows(fromNode, name, THROWS_ADDED, name);
        }
    }

    protected void removeAllThrows(ASTNameList fromNameList, SimpleNode toNode) {
        List<ASTName> names = getChildNames(fromNameList);
        for (ASTName name : names) {
            changeThrows(name, toNode, THROWS_REMOVED, name);
        }
    }

    protected void compareEachThrow(ASTNameList fromNameList, ASTNameList toNameList) {
        List<ASTName> fromNames = getChildNames(fromNameList);
        List<ASTName> toNames = getChildNames(toNameList);

        for (int fromIdx = 0; fromIdx < fromNames.size(); ++fromIdx) {
            // save a reference to the name here, in case it gets removed
            // from the array in getMatch.
            ASTName fromName = fromNames.get(fromIdx);

            int throwsMatch = getMatch(fromNames, fromIdx, toNames);

            if (throwsMatch == fromIdx) {
                continue;
            }
            else if (throwsMatch >= 0) {
                ASTName toName = ThrowsUtil.getNameNode(toNameList, throwsMatch);
                String fromNameStr = SimpleNodeUtil.toString(fromName);
                changed(fromName, toName, THROWS_REORDERED, fromNameStr, fromIdx, throwsMatch);
            }
            else {
                changeThrows(fromName, toNameList, THROWS_REMOVED, fromName);
            }
        }

        for (int toIdx = 0; toIdx < toNames.size(); ++toIdx) {
            if (toNames.get(toIdx) != null) {
                ASTName toName = ThrowsUtil.getNameNode(toNameList, toIdx);
                changeThrows(fromNameList, toName, THROWS_ADDED, toName);
            }
        }
    }

    protected int getMatch(List<ASTName> fromNames, int fromIdx, List<ASTName> toNames) {
        String fromNameStr = SimpleNodeUtil.toString(fromNames.get(fromIdx));

        for (int toIdx = 0; toIdx < toNames.size(); ++toIdx) {
            if (toNames.get(toIdx) != null && SimpleNodeUtil.toString(toNames.get(toIdx)).equals(fromNameStr)) {
                fromNames.set(fromIdx, null);
                toNames.set(toIdx, null); // mark as consumed
                return toIdx;
            }
        }

        return -1;
    }
}
