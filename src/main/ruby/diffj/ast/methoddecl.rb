#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/typeitemdecl'
require 'diffj/ast/type'
require 'diffj/ast/method'

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
    differ = DiffJ::MethodComparator.new filediffs
    differ.compare_access SimpleNodeUtil.getParent(from), SimpleNodeUtil.getParent(to)
    differ.compare_xxx from, to
  end

  def get_name methdecl
    MethodUtil.getFullName methdecl
  end

  def get_added_message methdecl
    DiffJ::TypeComparator::METHOD_ADDED
  end

  def get_removed_message methdecl
    DiffJ::TypeComparator::METHOD_REMOVED
  end
end
