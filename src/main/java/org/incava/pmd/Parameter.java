package org.incava.pmd;

import java.util.*;
import net.sourceforge.pmd.ast.*;


/**
 * Wraps an ASTFormalParameter
 */
public class Parameter {

    private final ASTFormalParameter param;

    private final String name;

    private final String type;

    public Parameter(ASTFormalParameter param) {
        this.param = param;

        if (param == null) {
            this.name = null;
            this.type = null;
        }
        else {
            ASTVariableDeclaratorId vid = (ASTVariableDeclaratorId)param.jjtGetChild(1);
            this.name = vid.getFirstToken().image;

            // type is the first child, but we also have to look for the
            // variable ID including brackets, for arrays
            StringBuilder sb = new StringBuilder();
            ASTType      type    = (ASTType)SimpleNodeUtil.findChild(param, ASTType.class);
            Token        ttk     = type.getFirstToken();
        
            while (true) {
                sb.append(ttk.image);
                if (ttk == type.getLastToken()) {
                    break;
                }
                else {
                    ttk = ttk.next;
                }
            }
            
            Token vtk = vid.getFirstToken();
            while (vtk != vid.getLastToken()) {
                vtk = vtk.next;
                sb.append(vtk.image);
            }

            this.type = sb.toString();
        }
    }

    public ASTFormalParameter getParameter() {
        return param;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return param.toString();
    }

}
