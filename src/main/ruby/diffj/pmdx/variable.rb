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
    vid.token(0).image
  end

  def nametk
    vid = find_child "net.sourceforge.pmd.ast.ASTVariableDeclaratorId"
    vid.token(0)
  end
end

