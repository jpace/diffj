
  def get_first_method_parameter compunit
    td    = process_node_subnode Java::NetSourceforgePmdAst::ASTTypeDeclaration,                 compunit    
    coid  = process_node_subnode Java::NetSourceforgePmdAst::ASTClassOrInterfaceDeclaration,     td
    coib  = process_node_subnode Java::NetSourceforgePmdAst::ASTClassOrInterfaceBody,            coid
    coibd = process_node_subnode Java::NetSourceforgePmdAst::ASTClassOrInterfaceBodyDeclaration, coib
    md    = process_node_subnode Java::NetSourceforgePmdAst::ASTMethodDeclaration,               coibd
    mdc   = process_node_subnode Java::NetSourceforgePmdAst::ASTMethodDeclarator,                md, 1
    fps   = process_node_subnode Java::NetSourceforgePmdAst::ASTFormalParameters,                mdc
    fp    = process_node_subnode Java::NetSourceforgePmdAst::ASTFormalParameter,                 fps
    fp
  end
  
  def test_nonvarargs
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { void method(Object args) {} }", "1.6"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"
    
    nodes = compunit.nodes
    info "cu.nodes: #{compunit.nodes}"
    
    assert_equal 1, nodes.size
    td = nodes[0]

    param = get_first_method_parameter compunit

    dump_node param, "####".bold

    tokens = param.tokens
    tokens.each do |tk|
      info "    #{tk}".bold
    end
  end
  
  def test_varargs
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { void method(Object ... args) {} }", "1.6"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"
    
    nodes = compunit.nodes
    info "cu.nodes: #{compunit.nodes}"
    
    assert_equal 1, nodes.size
    td = nodes[0]

    param = get_first_method_parameter compunit

    dump_node param, "####".bold

    tokens = param.tokens
    tokens.each do |tk|
      info "    #{tk}".bold
    end
  end

  def test_c_array
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { void method(Object[] args) {} }", "1.6"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"
    
    nodes = compunit.nodes
    info "cu.nodes: #{compunit.nodes}"
    
    assert_equal 1, nodes.size
    td = nodes[0]

    param = get_first_method_parameter compunit

    tokens = param.tokens
    tokens.each do |tk|
      info "    #{tk}".bold
    end
  end
