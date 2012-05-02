#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/tc'
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
    # info "compunit: #{compunit}"
    
    nodes = compunit.nodes
    # info "cu.nodes: #{compunit.nodes}"
    
    assert_equal 1, nodes.size
    td = nodes[0]

    params = get_method_parameters compunit

    # dump_node params, "####".bold

    if false
      tokens = params.tokens
      tokens.each do |tk|
        info "    #{tk}".bold
      end
    end

    params.nodes.each do |param|
      # info "param: #{param}"
      # info "param.to_string: #{param.to_string}"
      # info "param.typestr: #{param.typestr}"
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

    orig_score = from_params.match_score_orig to_params
    info "orig_score: #{orig_score}".bold.green
    
    score = from_params.match_score to_params
    info "score: #{score}"
    
    score = (score * 100).round / 100.0
    
    assert_equal exp, score, "#{from_decl} <=> #{to_decl}"
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
  
  def test_params_exact_match
    assert_match_score 1.0,  "A a",           "A a"
    assert_match_score 1.0,  "A a, B b",      "A a, B b"
    assert_match_score 1.0,  "A a, B b, C c", "A a, B b, C c"
  end

  def test_params_position_change
    assert_match_score 0.75,  "A a, B b",      "B b, A a"
    assert_match_score 0.83,  "A a, B b, C c", "B b, A a, C c"
  end  

  def test_params_added
    assert_match_score 0.75,  "A a",           "A a, B b"
    assert_match_score 0.63,  "A a",           "B b, A a"
    assert_match_score 0.83,  "A a, B b",      "A a, B b, C c"
    assert_match_score 0.75,  "A a, B b",      "A a, C c, B b"
  end
  
  def test_multiple_params
    assert_match_score 1.0,  "Object x, String y",          "Object x, String y"
    assert_match_score 1.0,  "int[] a, double b, String c", "int[] x, double y, String z"
    assert_match_score 0.83, "int[] a, double b, String c", "double x, int[] y, String z"
    assert_match_score 0.67, "int[] a, double b",           "double x, int[] y, String z"
    assert_match_score 0.58, "int[] a, double b, String c", "String x"
    assert_match_score 0.5,  "int[] a, double b",           "String x"
  end

end
