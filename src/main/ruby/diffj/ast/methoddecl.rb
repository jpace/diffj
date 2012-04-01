#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/typeitemdecl'

include Java

import org.incava.diffj.MethodDiff
import org.incava.pmdx.MethodUtil

module DiffJ; end

class DiffJ::MethodDeclComparator < DiffJ::TypeItemDeclComparator
  include Loggable

  def initialize diffs
    super diffs, "net.sourceforge.pmd.ast.ASTMethodDeclaration"
  end

  def get_score amd, bmd
    MethodUtil.getMatchScore amd, bmd
  end

  def do_compare from, to
    differ = MethodDiff.new(getFileDiffs())
    differ.compareAccess(SimpleNodeUtil.getParent(from), SimpleNodeUtil.getParent(to))
    differ.compare(from, to)
  end

  def get_name methdecl
    MethodUtil.getFullName methdecl
  end

  def get_added_message methdecl
    TypeDiff::METHOD_ADDED
  end

  def get_removed_message methdecl
    TypeDiff::METHOD_REMOVED
  end
end
