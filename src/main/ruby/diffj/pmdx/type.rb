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
end
