#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/typeitemdecl'
require 'diffj/ast/type'

include Java

import org.incava.diffj.CtorDiff
import org.incava.pmdx.CtorUtil

class DiffJ::CtorDeclComparator < DiffJ::TypeItemDeclComparator
  include Loggable

  def initialize diffs
    super diffs, "net.sourceforge.pmd.ast.ASTConstructorDeclaration"
  end

  def get_score from_ctor, to_ctor
    CtorUtil.getMatchScore from_ctor, to_ctor
  end

  def do_compare from, to
    differ = CtorDiff.new filediffs
    differ.compare_access SimpleNodeUtil.getParent(from), SimpleNodeUtil.getParent(to)
    differ.compare from, to
  end

  def get_name ctordecl
    CtorUtil.getFullName ctordecl
  end

  def get_added_message ctordecl
    DiffJ::TypeComparator::CONSTRUCTOR_ADDED
  end

  def get_removed_message ctordecl
    DiffJ::TypeComparator::CONSTRUCTOR_REMOVED
  end
end
