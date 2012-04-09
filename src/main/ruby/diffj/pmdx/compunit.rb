#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

class Java::net.sourceforge.pmd.ast::ASTCompilationUnit
  def package
    find_child "net.sourceforge.pmd.ast.ASTPackageDeclaration"
  end

  def imports
    find_children "net.sourceforge.pmd.ast.ASTImportDeclaration"
  end

  def type_declarations
    find_children "net.sourceforge.pmd.ast.ASTTypeDeclaration"
  end
end
