package org.incava.diffj.function;

import java.util.List;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.Messages;
import org.incava.diffj.element.Differences;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.ThrowsUtil;

public class Throws {
    public static final String THROWS_REMOVED = "throws removed: {0}";
    public static final String THROWS_ADDED = "throws added: {0}";
    public static final String THROWS_REORDERED = "throws {0} reordered from argument {1} to {2}";

    private final SimpleNode node;
    private final ASTNameList nameList;
    
    public Throws(SimpleNode node, ASTNameList nameList) {
        this.node = node;
        this.nameList = nameList;
    }

    public boolean isEmpty() {
        return nameList == null;
    }

    public void diff(Throws toThrows, Differences differences) {
        if (isEmpty()) {
            if (!toThrows.isEmpty()) {
                addAllThrows(toThrows, differences);
            }
        }
        else if (toThrows.isEmpty()) {
            removeAllThrows(toThrows, differences);
        }
        else {
            compareEachThrow(toThrows, differences);
        }
    }

    public List<ASTName> getChildNames() {
        return SimpleNodeUtil.snatchChildren(nameList, "net.sourceforge.pmd.ast.ASTName");
    }

    protected void changeThrows(SimpleNode fromNode, SimpleNode toNode, String msg, ASTName name, Differences differences) {
        differences.changed(fromNode, toNode, msg, SimpleNodeUtil.toString(name));
    }

    protected void addAllThrows(Throws toThrows, Differences differences) {
        List<ASTName> names = toThrows.getChildNames();
        for (ASTName name : names) {
            changeThrows(node, name, THROWS_ADDED, name, differences);
        }
    }

    protected void removeAllThrows(Throws toThrows, Differences differences) {
        List<ASTName> names = getChildNames();
        for (ASTName name : names) {
            changeThrows(name, toThrows.node, THROWS_REMOVED, name, differences);
        }
    }

    protected ASTName getName(int idx) {
        return ThrowsUtil.getNameNode(nameList, idx);
    }

    protected void compareEachThrow(Throws toThrows, Differences differences) {
        List<ASTName> fromNames = getChildNames();
        List<ASTName> toNames = toThrows.getChildNames();

        for (int fromIdx = 0; fromIdx < fromNames.size(); ++fromIdx) {
            // save a reference to the name here, in case it gets removed
            // from the array in getMatch.
            ASTName fromName = fromNames.get(fromIdx);
            int throwsMatch = getMatch(fromNames, fromIdx, toNames);
            
            if (throwsMatch == fromIdx) {
                continue;
            }
            else if (throwsMatch >= 0) {
                ASTName toName = toThrows.getName(throwsMatch);
                String fromNameStr = SimpleNodeUtil.toString(fromName);
                differences.changed(fromName, toName, THROWS_REORDERED, fromNameStr, fromIdx, throwsMatch);
            }
            else {
                changeThrows(fromName, toThrows.nameList, THROWS_REMOVED, fromName, differences);
            }
        }

        for (int toIdx = 0; toIdx < toNames.size(); ++toIdx) {
            if (toNames.get(toIdx) != null) {
                ASTName toName = toThrows.getName(toIdx);
                changeThrows(nameList, toName, THROWS_ADDED, toName, differences);
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
