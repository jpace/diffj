#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/typeitemdecl'
require 'diffj/ast/type'

include Java

class DiffJ::InnerTypeComparator < DiffJ::TypeItemDeclComparator
  include Loggable

  def initialize diffs, typecomp
    super diffs, "net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration"
    @typecomp = typecomp
  end

  def get_score from_coid, to_coid
    from_coid.match_score to_coid
  end

  def do_compare from, to
    info "from: #{from}; #{from.class}"
    info "to: #{to}; #{to.class}"
    @typecomp.compare_coids from, to
  end

  def get_name coid
    coid.namestr
  end

  def get_added_message coid
    coid.interface? ? DiffJ::TypeComparator::INNER_INTERFACE_ADDED : DiffJ::TypeComparator::INNER_CLASS_ADDED
  end

  def get_removed_message coid
    coid.interface? ? DiffJ::TypeComparator::INNER_INTERFACE_REMOVED : DiffJ::TypeComparator::INNER_CLASS_REMOVED
  end
end
