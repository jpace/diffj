#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/typeitemdecl'

include Java

import org.incava.diffj.FieldDiff
import org.incava.pmdx.FieldUtil

class DiffJ::FieldDeclComparator < DiffJ::TypeItemDeclComparator
  include Loggable

  def initialize diffs
    super diffs, "net.sourceforge.pmd.ast.ASTFieldDeclaration"
  end

  def get_score amd, bmd
    FieldUtil.getMatchScore amd, bmd
  end

  def do_compare from, to
    differ = FieldDiff.new(getFileDiffs())
    differ.compareAccess(SimpleNodeUtil.getParent(from), SimpleNodeUtil.getParent(to))
    differ.compare(from, to)
  end

  def get_name fielddecl
    FieldUtil.getNames fielddecl
  end

  def get_added_message methdecl
    TypeDiff::FIELD_ADDED
  end

  def get_removed_message methdecl
    TypeDiff::FIELD_REMOVED
  end
end
