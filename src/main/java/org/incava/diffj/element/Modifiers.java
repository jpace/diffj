package org.incava.diffj.element;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.SimpleNodeUtil;

public abstract class Modifiers {
    public static final Message MODIFIER_REMOVED = new Message("modifier removed: {0}");
    public static final Message MODIFIER_ADDED = new Message("modifier added: {0}");
    public static final Message MODIFIER_CHANGED = new Message("modifier changed from {0} to {1}");

    private final SimpleNode node;

    public Modifiers(SimpleNode node) {
        this.node = node;
    }

    public abstract int[] getModifierTypes();

    public void diff(Modifiers toModifiers, Differences differences) {
        int[] modifierTypes = getModifierTypes();

        Map<Integer, Token> fromByKind = getModifierMap();
        Map<Integer, Token> toByKind = toModifiers.getModifierMap();

        for (int modType : modifierTypes) {
            Token fromMod = fromByKind.get(modType);
            Token toMod = toByKind.get(modType);
            
            if (fromMod == null) {
                if (toMod != null) {
                    differences.changed(node.getFirstToken(), toMod, MODIFIER_ADDED, toMod.image);
                }
            }
            else if (toMod == null) {
                differences.changed(fromMod, toModifiers.node.getFirstToken(), MODIFIER_REMOVED, fromMod.image);
            }
        }
    }

    /**
     * Returns a map from token types ("kinds", as integers), to the token. This
     * assumes that there are no leading tokens of the same type for the given
     * node.
     */
    protected Map<Integer, Token> getModifierMap() {
        List<Token> mods = SimpleNodeUtil.getLeadingTokens(node);
        Map<Integer, Token> byKind = new TreeMap<Integer, Token>();

        for (Token tk : mods) {
            byKind.put(tk.kind, tk);
        }

        return byKind;
    }
}
