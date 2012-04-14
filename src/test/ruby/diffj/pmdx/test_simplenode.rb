#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj/diffjtestcase'
require 'diffj/io/file'

include Java

class DiffJ::SimpleNodeTestCase < DiffJ::TestCase
  include Loggable
  
  def dump_node node, indent = "", recurse = true
    info "#{indent}#{node.inspect}"
    
    tk = node.get_first_token
    ltk = node.get_last_token

    while tk != ltk
      info "#{indent}tk: #{tk.inspect}"
      info "#{indent}tk: #{tk.kind}; #{tk.image}"
      tk = tk.next
    end

    info "#{indent}tk: #{tk.inspect}"
    info "#{indent}tk: #{tk.kind}; #{tk.image}"

    ldg = node.leading_tokens 
    info "ldg: #{ldg}"

    n_children = node.jjt_get_num_children
    info "#{indent}n_children: #{n_children}"

    (0 ... n_children).each do |ci|
      child = node.jjt_get_child ci
      info "#{indent}child: #{child}".yellow

      if recurse
        dump_node child, indent + "    ", recurse
      end
    end

    tokens = node.tokens
    tokens.each do |tk|
      info "#{indent}#{tk}".yellow
    end
  end
  
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
    
    vd = process_parent_subnode fd, 1
    vdid = process_parent_subnode vd
  end

  def process_node_subnode cls, parent, idx = 0
    jjtchild = parent.jjt_get_child idx
    child = parent.node idx

    assert_equal jjtchild, child
    assert_same jjtchild, child

    assert_instance_of cls, child

    child
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

    td    = process_node_subnode Java::NetSourceforgePmdAst::ASTTypeDeclaration,                 compunit    
    coid  = process_node_subnode Java::NetSourceforgePmdAst::ASTClassOrInterfaceDeclaration,     td
    coib  = process_node_subnode Java::NetSourceforgePmdAst::ASTClassOrInterfaceBody,            coid
    coibd = process_node_subnode Java::NetSourceforgePmdAst::ASTClassOrInterfaceBodyDeclaration, coib
    fd    = process_node_subnode Java::NetSourceforgePmdAst::ASTFieldDeclaration,                coibd
    typ   = process_node_subnode Java::NetSourceforgePmdAst::ASTType,                            fd, 0
    pt    = process_node_subnode Java::NetSourceforgePmdAst::ASTPrimitiveType,                   typ
    vd    = process_node_subnode Java::NetSourceforgePmdAst::ASTVariableDeclarator,              fd, 1

    dump_node td, "####".cyan, true
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

    dump_node compunit, "----".cyan, true
    
    td    = process_subnode_leading_tokens [ "class" ],              compunit
    coid  = process_subnode_leading_tokens [ "class", "Test", "{" ], td
    coib  = process_subnode_leading_tokens [ "{", "int" ],           coid
    coibd = process_subnode_leading_tokens %w{ int },                coib
    fd    = process_subnode_leading_tokens %w{ int },                coibd
    typ   = process_subnode_leading_tokens %w{ int },                fd, 0
    pt    = process_subnode_leading_tokens %w{ },                    typ
    vd    = process_subnode_leading_tokens %w{ i },                  fd, 1
    vdid  = process_subnode_leading_tokens %w{ },                    vd

    dump_node td, "####".cyan, true
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
    info "ldg: #{ldg}".on_red
    
    assert_tokens_match explist, ldg
  end

  def to_msg tk, indent
    sprintf "%s%-20s %s", indent, tk.to_s, tk.inspect
  end

  def dump_tokens_up_to fromtk, tk
  end
  
  def dump_nt node, indent = ""
    info "node: #{node}".blue

    info "#{indent}subnodes".on_blue
    subnodes = node.nodes
    subnodes.each_with_index do |nd, nidx|
      info "#{indent}#{nd}".on_blue
      nftk = nd.first_token
      info to_msg(nftk, indent)
      nltk = nd.last_token
      info to_msg(nltk, indent)
    end

    info "#{indent}tokens".on_yellow
    tokens = node.tokens
    tokens.each do |tk|
      info "#{indent}#{tk}".yellow
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
end
