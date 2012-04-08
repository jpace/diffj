#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

class Java::net.sourceforge.pmd.ast::ASTConstructorDeclaration
  def parameters
    find_child "net.sourceforge.pmd.ast.ASTFormalParameters"
  end
  
  def match_score to
    from_params = parameters
    to_params = to.parameters
    org.incava.pmdx.ParameterUtil.getMatchScore from_params, to_params
  end

  # this is from function:
  def to_full_name tk, params
    types = org.incava.pmdx.ParameterUtil.getParameterTypes(params)
    ary = Array.new
    types.each do |type|
      ary << type
    end
    args  = ary.join ", "
    tk.image + "(" + args + ")"
  end

  def fullname
    name_tk = find_token Java::net.sourceforge.pmd.ast.JavaParserConstants::IDENTIFIER
    params = parameters
    to_full_name name_tk, params
  end

  # this is common to ctors and methods:
  def throws_list
    it = children.iterator
    while it.hasNext()
      obj = it.next()
      if obj.kind_of?(Java::net.sourceforge.pmd.ast.Token) && obj.kind == Java::net.sourceforge.pmd.ast.JavaParserConstants::THROWS && it.hasNext()
        return it.next()
      end
    end
    nil
  end
end
