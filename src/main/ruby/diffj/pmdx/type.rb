#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'
require 'diffj/pmdx/function'

class Java::net.sourceforge.pmd.ast::ASTClassOrInterfaceDeclaration
  def nametk
    find_token Java::net.sourceforge.pmd.ast.JavaParserConstants::IDENTIFIER
  end
  
  def namestr
    nametk.image
  end
  
  def match_score to
    namestr == to.namestr ? 1.0 : 0.0
  end

  def declarations
    body = find_child "net.sourceforge.pmd.ast.ASTClassOrInterfaceBody"
    body && body.find_children("net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration")
  end
end

class Java::NetSourceforgePmdAst::ASTClassOrInterfaceBodyDeclaration
  def declaration clsname
    find_child clsname
  end
end

class Java::net.sourceforge.pmd.ast::ASTTypeDeclaration
  def nametk
    subnode = find_child "net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration"
    subnode && subnode.token(0).next
  end

  def namestr
    (tk = nametk) && tk.image
  end

  def type_node
    find_child "net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration"
  end
end
