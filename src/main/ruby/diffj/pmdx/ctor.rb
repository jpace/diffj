#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'
require 'diffj/pmdx/function'

class Java::net.sourceforge.pmd.ast::ASTConstructorDeclaration
  include DiffJ::AST::Function
  
  def parameters
    find_child "net.sourceforge.pmd.ast.ASTFormalParameters"
  end
  
  def match_score to
    from_params = parameters
    to_params = to.parameters
    org.incava.pmdx.ParameterUtil.getMatchScore from_params, to_params
  end

  def fullname
    name_tk = find_token Java::net.sourceforge.pmd.ast.JavaParserConstants::IDENTIFIER
    to_full_name name_tk, parameters
  end
end
