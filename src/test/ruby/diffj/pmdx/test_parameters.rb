#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'diffj/diffjtestcase'
require 'diffj/io/file'

include Java

class DiffJ::ParametersTestCase < DiffJ::TestCase
  include Loggable  

  def get_method_parameters compunit
    td    = assert_has_child Java::NetSourceforgePmdAst::ASTTypeDeclaration,                 compunit    
    coid  = assert_has_child Java::NetSourceforgePmdAst::ASTClassOrInterfaceDeclaration,     td
    coib  = assert_has_child Java::NetSourceforgePmdAst::ASTClassOrInterfaceBody,            coid
    coibd = assert_has_child Java::NetSourceforgePmdAst::ASTClassOrInterfaceBodyDeclaration, coib
    md    = assert_has_child Java::NetSourceforgePmdAst::ASTMethodDeclaration,               coibd
    mdc   = assert_has_child Java::NetSourceforgePmdAst::ASTMethodDeclarator,                md, 1
    fps   = assert_has_child Java::NetSourceforgePmdAst::ASTFormalParameters,                mdc
  end
  
  def get_params decl
    file = DiffJ::IO::File.new nil, "testfile.java", "class Test { void method(" + decl + ") {} }", "1.6"
    info "file: #{file}"

    compunit = file.compile
    info "compunit: #{compunit}"
    
    nodes = compunit.nodes
    info "cu.nodes: #{compunit.nodes}"
    
    assert_equal 1, nodes.size
    td = nodes[0]

    params = get_method_parameters compunit

    # dump_node params, "####".bold

    tokens = params.tokens
    tokens.each do |tk|
      info "    #{tk}".bold
    end

    params.nodes.each do |param|
      info "param: #{param}"
      info "param.to_string: #{param.to_string}"
      info "param.typestr: #{param.typestr}"
    end

    params
  end

  def assert_match_score exp, from_decl, to_decl
    info "from_decl: #{from_decl}"
    from_params = get_params from_decl
    info "from_params: #{from_params}"
    
    info "to_decl: #{to_decl}"
    to_params   = get_params to_decl
    info "to_params: #{to_params}"

    score = from_params.match_score to_params
    info "score: #{score}"
    assert_equal exp, score
  end

  def assert_typestr exp, decl
    params = get_params decl
    param = params[0]
    info "param: #{param}"

    dump_node param, "----".yellow

    assert_equal exp, param.typestr
  end

  def test_typestr
    assert_typestr "Object", "Object x"
    assert_typestr "Object", "Object y"

    assert_typestr "Object[]", "Object[] x"
    assert_typestr "Object[]", "Object []x"
    assert_typestr "Object[]", "Object x[]"

    assert_typestr "Object...",       "Object ... x"
    assert_typestr "List<Object>",    "List<Object> x"
    assert_typestr "List<Object>...", "List<Object> ... x"
    assert_typestr "List<Object>[]",  "List<Object>[] x"
    assert_typestr "Map<Long,String>[]",  "Map < Long, String >[] x"
  end
 
  def test_nonvarargs_same_type_same_name
    assert_match_score 1.0, "Object x", "Object x"
  end
  
  def test_nonvarargs_same_type_different_name
    assert_match_score 1.0, "Object x", "Object y"
  end
  
  def test_nonvarargs_different_type_same_name
    assert_match_score 0.5, "Object x", "String x"
  end
  
  def test_nonvarargs_different_type_different_name
    assert_match_score 0.5, "Object x", "String y"
  end
  
  def test_varargs_same_type_same_name
    assert_match_score 1.0, "Object ... x", "Object ... x"
  end
  
  def test_varargs_same_type_different_name
    assert_match_score 1.0, "Object ... x", "Object ... y"
  end
  
  def test_varargs_different_type_same_name
    assert_match_score 0.5, "Object ... x", "String ... x"
  end
  
  def test_varargs_different_type_different_name
    assert_match_score 0.5, "Object ... x", "String ... y"
  end
  
  def test_varargs_type_to_nonvarargs_type_same_name
    assert_match_score 0.5, "Object ... x", "Object x"
  end
  
  def test_varargs_type_to_nonvarargs_type_different_name
    assert_match_score 0.5, "Object ... x", "Object y"
  end
  
  def test_varargs_to_c_array_same_name
    assert_match_score 0.5, "Object ... x", "Object[] x"
  end
end
