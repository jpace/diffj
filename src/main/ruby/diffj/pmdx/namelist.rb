#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

class Java::net.sourceforge.pmd.ast::ASTNameList
  def name_node index
    find_child "net.sourceforge.pmd.ast.ASTName", index
  end
end
