#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

class Java::net.sourceforge.pmd.ast::ASTVariableDeclarator
  def name
    # should be namestr
    nametk
  end

  def namestr
    vid = find_child "net.sourceforge.pmd.ast.ASTVariableDeclaratorId"
    vid.first_token.image
  end

  def nametk
    vid = find_child "net.sourceforge.pmd.ast.ASTVariableDeclaratorId"
    vid.first_token
  end
end

