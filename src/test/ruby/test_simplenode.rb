#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffjtestcase'
require 'diffj/io/file'

include Java

class DiffJ::SimpleNodeTestCase < DiffJ::TestCase
  include Loggable
  
  TESTBED_DIR = '/proj/org/incava/diffj/src/test/resources'

  def dump_node node, indent = ""
    info "#{indent}#{node.inspect}"
    
    tk = node.get_first_token
    ltk = node.get_last_token

    while tk != ltk
      info "#{indent}tk: #{tk.inspect}"
      info "#{indent}tk: #{tk.kind}; #{tk.image}"
      tk = tk.next
    end

    ldg = node.leading_tokens 
    info "ldg: #{ldg}"

    n_children = node.jjt_get_num_children
    info "#{indent}n_children: #{n_children}"

    (0 ... n_children).each do |ci|
      child = node.jjt_get_child ci
      info "#{indent}child: #{child}".yellow

      dump_node child, indent + "    "
    end

    tokens = node.tokens
    tokens.each do |tk|
      info "#{indent}#{tk}".yellow
    end

    nodestokens = node.nodes_and_tokens
    nodestokens.each do |nt|
      info "#{indent}#{nt}".green
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
  
  def test_basic
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { int i; }", "1.5"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"
    
    info "compunit.inspect: #{compunit.inspect}"

    info "compunit.size: #{compunit.size}"
    info "compunit.length: #{compunit.length}"

    # info "self.methods: #{self.methods.sort}".yellow

    assert_equal 1, compunit.size
    assert_equal 1, compunit.length

    parent = compunit.parent
    info "cu.parent: #{compunit.parent}"

    assert_nil compunit.parent

    nodes = compunit.nodes
    info "cu.nodes: #{compunit.nodes}"

    assert_equal 1, nodes.size
    td = nodes[0]
    
    assert_kind_of Java::net.sourceforge.pmd.ast.ASTTypeDeclaration, td
    assert_instance_of Java::net.sourceforge.pmd.ast.ASTTypeDeclaration, td

    assert_equal td, compunit.node(0)
    assert_same td, compunit.node(0)

    tk = compunit.get_first_token
    info "tk: #{tk.inspect}"
    info "tk: #{tk.kind}; #{tk.image}"

    n_children = compunit.jjt_get_num_children
    info "n_children: #{n_children}"

    child = compunit.jjt_get_child 0
    info "child: #{child}"

    dump_node compunit
    
    # assert_equal 0, compunit.childtokens.length
  end

  def test_leading_tokens
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { int i; }", "1.5"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"
    
    info "compunit.inspect: #{compunit.inspect}"

    info "compunit.size: #{compunit.size}"
    info "compunit.length: #{compunit.length}"

    # info "self.methods: #{self.methods.sort}".yellow

    assert_equal 1, compunit.size
    assert_equal 1, compunit.length

    parent = compunit.parent
    info "cu.parent: #{compunit.parent}"

    assert_nil compunit.parent

    nodes = compunit.nodes
    info "cu.nodes: #{compunit.nodes}"

    assert_equal 1, nodes.size
    td = nodes[0]
    
    assert_kind_of Java::net.sourceforge.pmd.ast.ASTTypeDeclaration, td
    assert_instance_of Java::net.sourceforge.pmd.ast.ASTTypeDeclaration, td

    assert_equal td, compunit.node(0)
    assert_same td, compunit.node(0)

    tk = compunit.get_first_token
    info "tk: #{tk.inspect}"
    info "tk: #{tk.kind}; #{tk.image}"

    n_children = compunit.jjt_get_num_children
    info "n_children: #{n_children}"

    child = compunit.jjt_get_child 0
    info "child: #{child}"

    dump_node compunit

    # assert_equal 0, compunit.childtokens.length
  end

  def dump_nt node, indent = ""
    info "node: #{node}".blue
    tokens = node.tokens
    tokens.each do |tk|
      info "#{indent}#{tk}".yellow
    end

    nodestokens = node.nodes_and_tokens
    nodestokens.each do |nt|
      info "#{indent}#{nt}".green
    end

    node.nodes.each do |n|
      dump_nt n, indent + "  "
    end
  end

  def test_nodes_and_tokens
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { static int i; }", "1.5"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"
    
    info "compunit.inspect: #{compunit.inspect}"

    dump_nt compunit
  end    

end
