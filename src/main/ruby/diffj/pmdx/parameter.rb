#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

class Java::net.sourceforge.pmd.ast::ASTFormalParameters
  def parameters
    find_children "net.sourceforge.pmd.ast.ASTFormalParameter"
  end

  def get_parameter index
    return find_child "net.sourceforge.pmd.ast.ASTFormalParameter", index
  end

  def get_parameter_nametk index
    param = get_parameter index
    param && param.nametk
  end

  def get_parameter_names
    parameters.collect { |p| p.nametk }
  end

  def get_parameter_types
    # stack "self: #{self.object_id}".bold
    unless defined? @param_types
      @param_types = nodes.collect { |node| node.typestr }
    end
    @param_types
  end

  def get_list_match from_list, from_index, to_list
    to_size = to_list.size

    from_str = from_list[from_index]
    to_str = to_list[from_index]

    return -1 if from_str.nil?
    
    if from_str == to_str
      from_list[from_index] = nil
      to_list[from_index] = nil
      return from_index
    end
    
    to_list.each_with_index do |to_str, to_idx|
      if from_str == to_str
        from_list[from_index] = nil
        to_list[to_idx] = nil
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
    match_score_orig to
  end

  def match_score_orig to
    return 1.0 if size == 0 && to.size == 0
    
    from_param_types = get_parameter_types.dup
    to_param_types   = to.get_parameter_types.dup

    # info "from_param_types: #{from_param_types}"
    # info "to_param_types: #{to_param_types}"
    
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

  def match_score_new to
    return 1.0 if size == 0 && to.size == 0
    
    from_param_types = get_parameter_types
    to_param_types   = to.get_parameter_types

    # info "from_param_types: #{from_param_types}"
    # info "to_param_types: #{to_param_types}"    
    
    return 1.0 if from_param_types == to_param_types

    max_params = [ from_param_types.size, to_param_types.size ].max

    from_param_types = from_param_types.dup
    to_param_types   = to_param_types.dup

    n_exact = 0
    n_misordered = 0

    (0 ... [ from_param_types.length, to_param_types.length ].min).each do |idx|
      if from_param_types[idx] == to_param_types[idx]
        n_exact += 1
        from_param_types[idx] = nil
        to_param_types[idx] = nil
      end
    end

    from_param_types.compact!
    to_param_types.compact!

    info "from_param_types: #{from_param_types}"
    info "to_param_types: #{to_param_types}"

    from_param_types.each do |fp|
      if del = to_param_types.delete(fp)
        n_misordered += 1
      end
    end

    to_param_types.each do |tp|
      if del = from_param_types.delete(tp)
        n_misordered += 1
      end
    end

    match = n_exact.to_f / max_params
    match += n_misordered.to_f / (2 * max_params)
    
    0.5 + (match / 2.0)
  end
end

class Java::net.sourceforge.pmd.ast::ASTFormalParameter
  def nametk
    self[1].token(0)
  end

  def namestr
    nametk.image
  end

  def typestr
    # type is the first child, but we also have to look for the variable ID
    # including brackets, for arrays
    str = ""

    # handle "Object ary[]", those silly geese:
    tkns = tokens
    if tkns[-1].image == ']'
      str << tkns.pop.image
      if tkns[-1].image == '['
        str = tkns.pop.image + str
      end
    end

    # remove the variable name:
    tkns.pop

    tkns.collect { |tk| tk.image }.join('') + str
  end
end
