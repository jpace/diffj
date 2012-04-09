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
    (0 ... nParams).each do |idx|
      param = jjt_get_child idx
      type  = param.typestr
      types.add type
    end
    types
  end

  def get_list_match from_list, from_index, to_list
    from_size = from_list.size
    to_size = to_list.size
    from_str = from_index < from_size ? from_list.get(from_index) : nil
    to_str = from_index < to_size ? to_list.get(from_index) : nil
        
    return -1 if from_str.nil?
    
    if from_str == to_str
      from_list.set from_index, nil
      to_list.set from_index, nil
      return from_index
    end
    
    (0 ... to_size).each do |to_idx|
      to_str = to_list.get to_idx
      if from_str == to_str
        from_list.set(from_index, nil)
        to_list.set(to_idx, nil)
        return to_idx
      end
    end
    -1
  end

  def count_matches x_param_types, y_param_types
    exact_matches = 0
    misordered_matches = 0
    
    (0 ... x_param_types.size).each do |idx|
      param_match = get_list_match x_param_types, idx, y_param_types
      if param_match == idx
        exact_matches += 1
      elsif param_match >= 0
        misordered_matches += 1
      end
    end

    [ exact_matches, misordered_matches ]
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
    
    match_counts = count_matches from_param_types, to_param_types

    exact_matches = match_counts[0]
    misordered_matches = match_counts[1]

    match_counts = count_matches to_param_types, from_param_types

    exact_matches += match_counts[0]
    misordered_matches += match_counts[1]

    num_params = [ from_param_types.size, to_param_types.size ].max
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
