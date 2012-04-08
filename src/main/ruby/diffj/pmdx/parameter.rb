#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

class Java::net.sourceforge.pmd.ast::ASTFormalParameters
  def parameters
    snatch_children "net.sourceforge.pmd.ast.ASTFormalParameter"
  end

  def get_parameter index
    return find_child "net.sourceforge.pmd.ast.ASTFormalParameter", index
  end

  def get_parameter_nametk index
    param = get_parameter index
    param && param.nametk
  end

  def get_parameter_names
    fps = parameters
    names = java.util.ArrayList.new
    fps.each do |fp|
      names.add fp.nametk
    end
    names
  end

  def match_score to
    org.incava.pmdx.ParameterUtil.getMatchScore self, to
  end
end

class Java::net.sourceforge.pmd.ast::ASTFormalParameter
  def nametk
    vid = jjt_get_child 1
    vid.first_token
  end

  def namestr
    process unless @name
    @name
  end

  def typestr
    process unless @type
    @type
  end

  def process
    vid = jjtGetChild 1
    @name = vid.getFirstToken().image

    # type is the first child, but we also have to look for the
    # variable ID including brackets, for arrays
    @type = ""
    type = find_child "net.sourceforge.pmd.ast.ASTType"
    ttk     = type.getFirstToken()
        
    while true
      @type << ttk.image
      if ttk == type.getLastToken()
        break
      else
        ttk = ttk.next
      end
    end
            
    vtk = vid.getFirstToken()
    while vtk != vid.getLastToken()
      vtk = vtk.next;
      @type << vtk.image
    end
  end
end
