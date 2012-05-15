#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'
require 'diffj/ast/typeitemdecl'
require 'diffj/ast/field'

include Java

class DiffJ::FieldDeclComparator < DiffJ::TypeItemDeclComparator
  def initialize diffs
    super diffs, "net.sourceforge.pmd.ast.ASTFieldDeclaration"
  end

  def get_score from_field, to_field
    from_field.match_score to_field
  end

  if false
    # old one:
    def do_compare from, to
      differ = Java::org.incava.diffj.FieldDiff.new filediffs
      differ.compare_access from.parent, to.parent
      differ.compare from, to
    end
  else
    # new one
    def do_compare from, to
      differ = DiffJ::FieldComparator.new filediffs
      differ.compare_access from.parent, to.parent
      differ.compare from, to
    end
  end

  def get_name fielddecl
    fielddecl.names
  end

  def get_added_message fielddecl
    DiffJ::TypeComparator::FIELD_ADDED
  end

  def get_removed_message fielddecl
    DiffJ::TypeComparator::FIELD_REMOVED
  end
end
