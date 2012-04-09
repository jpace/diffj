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
    from_params.match_score to_params
  end

  def fullname
    nametk = find_token Java::net.sourceforge.pmd.ast.JavaParserConstants::IDENTIFIER
    to_full_name nametk, parameters
  end
end
