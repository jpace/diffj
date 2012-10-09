package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.ThrowsUtil;

public class Throws {
    private final SimpleNode node;
    private final ASTNameList nameList;
    
    public Throws(SimpleNode node, ASTNameList nameList) {
        this.node = node;
        this.nameList = nameList;
    }
    
    public void diff(SimpleNode toNode, ASTNameList toNameList, Differences differences) {
        if (nameList == null) {
            if (toNameList != null) {
                addAllThrows(toNameList, differences);
            }
        }
        else if (toNameList == null) {
            removeAllThrows(toNode, differences);
        }
        else {
            compareEachThrow(toNameList, differences);
        }
    }

    public List<ASTName> getChildNames(ASTNameList nameList) {
        return SimpleNodeUtil.snatchChildren(nameList, "net.sourceforge.pmd.ast.ASTName");
    }

    protected void changeThrows(SimpleNode fromNode, SimpleNode toNode, String msg, ASTName name, Differences differences) {
        differences.changed(fromNode, toNode, msg, SimpleNodeUtil.toString(name));
    }

    protected void addAllThrows(ASTNameList toNameList, Differences differences) {
        List<ASTName> names = getChildNames(toNameList);
        for (ASTName name : names) {
            changeThrows(node, name, Messages.THROWS_ADDED, name, differences);
        }
    }

    protected void removeAllThrows(SimpleNode toNode, Differences differences) {
        List<ASTName> names = getChildNames(nameList);
        for (ASTName name : names) {
            changeThrows(name, toNode, Messages.THROWS_REMOVED, name, differences);
        }
    }

    protected void compareEachThrow(ASTNameList toNameList, Differences differences) {
        List<ASTName> fromNames = getChildNames(nameList);
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
                differences.changed(fromName, toName, Messages.THROWS_REORDERED, fromNameStr, fromIdx, throwsMatch);
            }
            else {
                changeThrows(fromName, toNameList, Messages.THROWS_REMOVED, fromName, differences);
            }
        }

        for (int toIdx = 0; toIdx < toNames.size(); ++toIdx) {
            if (toNames.get(toIdx) != null) {
                ASTName toName = ThrowsUtil.getNameNode(toNameList, toIdx);
                changeThrows(nameList, toName, Messages.THROWS_ADDED, toName, differences);
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
