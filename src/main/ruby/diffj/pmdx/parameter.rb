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

  def get_parameter_types
    types = java.util.ArrayList.new
    nParams = jjt_get_num_children
    Log.info "nParams: #{nParams}".on_blue
    (0 ... nParams).each do |idx|
      param = jjtGetChild(idx);
      type  = param.typestr
      Log.info "type: #{type}".on_blue
      types.add type
    end
    Log.info "types: #{types}".on_blue
    types
  end

  def match_score to
    if jjtGetNumChildren() == 0 && to.jjtGetNumChildren() == 0
      return 1.0
    end
    
    # (int[], double, String) <=> (int[], double, String) ==> 100% (3 of 3)
    # (int[], double, String) <=> (double, int[], String) ==> 80% (3 of 3 - 10% * misordered)
    # (int[], double)         <=> (double, int[], String) ==> 46% (2 of 3 - 10% * misordered)
    # (int[], double, String) <=> (String) ==> 33% (1 of 3 params)
    # (int[], double) <=> (String) ==> 0 (0 of 3)

    org.incava.pmdx.ParameterUtil.getMatchScore self, to
  end
end

class Java::net.sourceforge.pmd.ast::ASTFormalParameter
  def initialize args
    super args
    Log.info "*******************************************************".on_red

    @type = nil
    @name = nil
  end

  def nametk
    vid = jjt_get_child 1
    vid.first_token
  end

  def namestr
    vid = jjtGetChild 1
    vid.getFirstToken().image
  end

  def typestr
    # type is the first child, but we also have to look for the
    # variable ID including brackets, for arrays
    str = ""
    type = find_child "net.sourceforge.pmd.ast.ASTType"
    ttk     = type.getFirstToken()
        
    while true
      str << ttk.image
      if ttk == type.getLastToken()
        break
      else
        ttk = ttk.next
      end
    end
            
    vid = jjtGetChild 1
    vtk = vid.getFirstToken()
    while vtk != vid.getLastToken()
      vtk = vtk.next;
      str << vtk.image
    end
    str
  end
end
