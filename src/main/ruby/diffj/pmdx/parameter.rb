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

  def get_list_match fromList, fromIndex, toList
    fromSize = fromList.size();
    toSize = toList.size();
    fromStr = fromIndex < fromSize ? fromList.get(fromIndex) : nil
    toStr = fromIndex < toSize ? toList.get(fromIndex) : nil
        
    return -1 if fromStr.nil?
    
    if fromStr == toStr
      fromList.set(fromIndex, nil)
      toList.set(fromIndex, nil)
      return fromIndex
    end
    
    (0 ... toSize).each do |toIdx|
      toStr = toList.get(toIdx);
      if fromStr == toStr
        fromList.set(fromIndex, nil)
        toList.set(toIdx, nil)
        return toIdx
      end
    end
    -1
  end

  def match_score to
    return 1.0 if jjt_get_num_children == 0 && to.jjt_get_num_children == 0
    
    # (int[], double, String) <=> (int[], double, String) ==> 100% (3 of 3)
    # (int[], double, String) <=> (double, int[], String) ==> 80% (3 of 3 - 10% * misordered)
    # (int[], double)         <=> (double, int[], String) ==> 46% (2 of 3 - 10% * misordered)
    # (int[], double, String) <=> (String) ==> 33% (1 of 3 params)
    # (int[], double) <=> (String) ==> 0 (0 of 3)

    from_param_types = get_parameter_types
    to_param_types = to.get_parameter_types
    
    from_size = from_param_types.size
    to_size = to_param_types.size

    exact_matches = 0
    misordered_matches = 0

    (0 ... from_size).each do |from_idx|
      param_match = get_list_match from_param_types, from_idx, to_param_types
      if param_match == from_idx
        exact_matches += 1
      elsif param_match >= 0
        misordered_matches += 1
      end
    end

    (0 ... to_size).each do |to_idx|
      param_match = get_list_match to_param_types, to_idx, from_param_types
      if param_match == to_idx
        exact_matches += 1
      elsif param_match >= 0
        misordered_matches += 1
      end
    end

    num_params = [ from_size, to_size ].max
    match = exact_matches.to_f / num_params
    match += misordered_matches.to_f / (2 * num_params)

    0.5 + (match / 2.0)
  end
end

class Java::net.sourceforge.pmd.ast::ASTFormalParameter
  def nametk
    vid = jjt_get_child 1
    vid.first_token
  end

  def namestr
    nametk.image
  end

  def typestr
    # type is the first child, but we also have to look for the variable ID
    # including brackets, for arrays
    str = ""
    type = find_child "net.sourceforge.pmd.ast.ASTType"
    ttk = type.first_token
        
    while true
      str << ttk.image
      if ttk == type.last_token
        break
      else
        ttk = ttk.next
      end
    end
            
    vid = jjt_get_child 1
    vtk = vid.first_token
    while vtk != vid.last_token
      vtk = vtk.next;
      str << vtk.image
    end
    str
  end
end
