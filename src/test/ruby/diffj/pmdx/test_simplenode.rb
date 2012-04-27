#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'diffj/diffjtestcase'
require 'diffj/io/file'

include Java

class DiffJ::SimpleNodeTestCase < DiffJ::TestCase
  include Loggable
  
  def dump node, indent = "  "
    tk = compunit.get_first_token
    info "tk: #{tk.inspect}"
    info "tk: #{tk.kind}; #{tk.image}"

    n_children = compunit.jjt_get_num_children
    info "n_children: #{n_children}"

    child = compunit.jjt_get_child 0
    info "child: #{child}"
  end

  def assert_tokens exptokens, node
    assert_tokens_match exptokens, node.tokens
  end

  def process_tokens_subnode alltokens, from, to, parent, idx = 0
    subnode = parent.jjt_get_child idx
    assert_tokens alltokens[from .. to], subnode
    subnode
  end

  def test_tokens
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { int i; }", "1.5"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"
    
    nodes = compunit.nodes
    info "cu.nodes: #{compunit.nodes}"
    
    assert_equal 1, nodes.size
    td = nodes[0]

    # nil means eof:
    exptokens = [ "class", "Test", "{", "int", "i", ";", "}", nil ]

    assert_tokens_match exptokens, compunit.tokens

    dump_node compunit, "!!!!", true
    
    # typedecl has same tokens except for EOF (nil):
    td = compunit.node(0)
    assert_tokens exptokens[0 .. -2], td

    td    = process_tokens_subnode exptokens, 0, -2, compunit
    coid  = process_tokens_subnode exptokens, 0, -2, td
    coib  = process_tokens_subnode exptokens, 2, -2, coid
    coibd = process_tokens_subnode exptokens, 3,  5, coib
    fd    = process_tokens_subnode exptokens, 3,  5, coibd
    typ   = process_tokens_subnode exptokens, 3,  3, fd, 0
    pt    = process_tokens_subnode exptokens, 3,  3, typ
    vd    = process_tokens_subnode exptokens, 4,  4, fd, 1
    vdid  = process_tokens_subnode exptokens, 4,  4, vd
  end

  def assert_parent expparent, node
    assert_equal expparent, node.jjt_get_parent
    assert_equal expparent, node.parent
  end

  def process_parent_subnode parent, idx = 0
    child = parent.jjt_get_child idx
    assert_parent parent, child
    child
  end

  def test_parent
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { int i; }", "1.5"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"

    dump_node compunit, "!!!!", true

    assert_nil compunit.parent

    td    = process_parent_subnode compunit
    coid  = process_parent_subnode td
    coib  = process_parent_subnode coid
    coibd = process_parent_subnode coib
    fd    = process_parent_subnode coibd
    typ   = process_parent_subnode fd, 0
    pt    = process_parent_subnode typ
    vd    = process_parent_subnode fd, 1
    vdid  = process_parent_subnode vd
  end
  
  def test_nodes
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { int i; }", "1.5"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"
    
    nodes = compunit.nodes
    info "cu.nodes: #{compunit.nodes}"
    
    assert_equal 1, nodes.size
    td = nodes[0]

    td    = assert_has_child Java::NetSourceforgePmdAst::ASTTypeDeclaration,                 compunit    
    coid  = assert_has_child Java::NetSourceforgePmdAst::ASTClassOrInterfaceDeclaration,     td
    coib  = assert_has_child Java::NetSourceforgePmdAst::ASTClassOrInterfaceBody,            coid
    coibd = assert_has_child Java::NetSourceforgePmdAst::ASTClassOrInterfaceBodyDeclaration, coib
    fd    = assert_has_child Java::NetSourceforgePmdAst::ASTFieldDeclaration,                coibd
    typ   = assert_has_child Java::NetSourceforgePmdAst::ASTType,                            fd, 0
    pt    = assert_has_child Java::NetSourceforgePmdAst::ASTPrimitiveType,                   typ
    vd    = assert_has_child Java::NetSourceforgePmdAst::ASTVariableDeclarator,              fd, 1

    dump_node td, "####", true
  end

  def process_subnode_leading_tokens exptokens, parent, idx = 0
    subnode = parent.jjt_get_child idx
    assert_leading_tokens exptokens, subnode
    subnode
  end

  def test_leading_tokens
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { int i; }", "1.5"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"

    dump_node compunit, "----", true
    
    td    = process_subnode_leading_tokens [ "class" ],              compunit
    coid  = process_subnode_leading_tokens [ "class", "Test", "{" ], td
    coib  = process_subnode_leading_tokens [ "{", "int" ],           coid
    coibd = process_subnode_leading_tokens %w{ int },                coib
    fd    = process_subnode_leading_tokens %w{ int },                coibd
    typ   = process_subnode_leading_tokens %w{ int },                fd, 0
    pt    = process_subnode_leading_tokens %w{ },                    typ
    vd    = process_subnode_leading_tokens %w{ i },                  fd, 1
    vdid  = process_subnode_leading_tokens %w{ },                    vd

    dump_node td, "####", true
  end

  def assert_is_token tk, msg = nil
    assert_instance_of Java::NetSourceforgePmdAst::Token, tk, msg
  end

  def assert_eof_token tk, msg = nil
    assert_equal Java::net.sourceforge.pmd.ast.JavaParserConstants::EOF, tk.kind, msg
  end

  def assert_tokens_match explist, actlist
    assert_equal explist.size, actlist.size, "explist: #{explist.inspect}; actlist: #{actlist.inspect}"
    (0 ... [ explist.size, actlist.size ].max).each do |idx|
      assert_not_nil actlist[idx], "actlist[#{idx}] (explist[#{idx}]: #{explist[idx]}"
      if explist[idx]
        assert_is_token actlist[idx]
        assert_equal explist[idx], actlist[idx].image
      else
        assert_is_token actlist[idx]
        assert_eof_token actlist[idx]
      end
    end
  end

  def assert_leading_tokens explist, node
    ldg = node.leading_tokens
    info "ldg: #{ldg}"
    
    assert_tokens_match explist, ldg
  end

  def to_msg tk, indent
    sprintf "%s%-20s %s", indent, tk.to_s, tk.inspect
  end

  def dump_nt node, indent = ""
    info "node: #{node}"

    info "#{indent}subnodes"
    subnodes = node.nodes
    subnodes.each_with_index do |nd, nidx|
      info "#{indent}#{nd}"
      nftk = nd.first_token
      info to_msg(nftk, indent)
      nltk = nd.last_token
      info to_msg(nltk, indent)
    end

    info "#{indent}tokens"
    tokens = node.tokens
    tokens.each do |tk|
      info "#{indent}#{tk}"
    end

    children = node.all_children
    children.each do |child|
      msg = to_msg child, indent
      info msg.red
    end

    children = node.all_children
    children.each do |child|
      msg = to_msg child, indent
      info msg.bold
      if child.kind_of? Java::net.sourceforge.pmd.ast.SimpleNode
        dump_nt child, indent + "  "
      end
    end

    puts
  end

  def test_all_children
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { static int i; }", "1.5"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"
    
    info "compunit.inspect: #{compunit.inspect}"

    dump_nt compunit

    ndtks = compunit.all_children
    assert_equal 2, ndtks.size

    typedecl = ndtks[0]
    assert_kind_of Java::net.sourceforge.pmd.ast.ASTTypeDeclaration, typedecl

    eoftk = ndtks[1]
    assert_kind_of Java::net.sourceforge.pmd.ast.Token, eoftk
    assert_equal Java::net.sourceforge.pmd.ast.JavaParserConstants::EOF, eoftk.kind
  end    

  def get_first_method_parameter compunit
    td    = assert_has_child Java::NetSourceforgePmdAst::ASTTypeDeclaration,                 compunit    
    coid  = assert_has_child Java::NetSourceforgePmdAst::ASTClassOrInterfaceDeclaration,     td
    coib  = assert_has_child Java::NetSourceforgePmdAst::ASTClassOrInterfaceBody,            coid
    coibd = assert_has_child Java::NetSourceforgePmdAst::ASTClassOrInterfaceBodyDeclaration, coib
    md    = assert_has_child Java::NetSourceforgePmdAst::ASTMethodDeclaration,               coibd
    mdc   = assert_has_child Java::NetSourceforgePmdAst::ASTMethodDeclarator,                md, 1
    fps   = assert_has_child Java::NetSourceforgePmdAst::ASTFormalParameters,                mdc
    fp    = assert_has_child Java::NetSourceforgePmdAst::ASTFormalParameter,                 fps
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
end
