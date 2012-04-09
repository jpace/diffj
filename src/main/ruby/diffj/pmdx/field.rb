#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'
require 'set'

class Java::net.sourceforge.pmd.ast::ASTFieldDeclaration
  def variable_declarators
    find_children "net.sourceforge.pmd.ast.ASTVariableDeclarator"
  end

  def name_list
    variable_declarators.collect { |vd| vd.name.image }
  end

  def names
    name_list.join ", "
  end

  def match_score to
    from_names = name_list
    to_names = to.name_list

    matched = (from_names & to_names).size
    count = [ from_names.size, to_names.size ].max
    score = 0.5 * matched / count

    from_type = find_child "net.sourceforge.pmd.ast.ASTType"
    to_type = to.find_child "net.sourceforge.pmd.ast.ASTType"

    if from_type.to_string == to_type.to_string
      score += 0.5
    end
    score
  end
end

