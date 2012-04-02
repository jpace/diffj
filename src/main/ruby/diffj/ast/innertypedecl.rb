#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/typeitemdecl'

include Java

import org.incava.diffj.TypeDiff
import org.incava.pmdx.ClassUtil

class DiffJ::InnerTypeComparator < DiffJ::TypeItemDeclComparator
  include Loggable

  def initialize diffs, typecomp
    super diffs, "net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration"
    @typecomp = typecomp
  end

  def get_score from_ctor, to_ctor
    ClassUtil.getMatchScore from_ctor, to_ctor
  end

  def do_compare from, to
    info "from: #{from}; #{from.class}".yellow
    info "to: #{to}; #{to.class}".yellow
    @typecomp.compare_coids from, to
  end

  def get_name coid
    ClassUtil.getName(coid).image
  end

  def get_added_message coid
    coid.isInterface() ? TypeDiff::INNER_INTERFACE_ADDED : TypeDiff::INNER_CLASS_ADDED
  end

  def get_removed_message coid
    coid.isInterface() ? TypeDiff::INNER_INTERFACE_REMOVED : TypeDiff::INNER_CLASS_REMOVED
  end
end
