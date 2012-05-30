#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/ast/typeitemdecl'
require 'diffj/ast/type'
require 'diffj/ast/method'
require 'diffj/pmdx'

module DiffJ
  class MethodDeclComparator < DiffJ::TypeItemDeclComparator
    def initialize diffs
      super diffs, "net.sourceforge.pmd.ast.ASTMethodDeclaration"
    end

    def get_score from, to
      from.match_score to
    end

    def do_compare from, to
      differ = DiffJ::MethodComparator.new filediffs
      differ.compare_access from.parent, to.parent
      differ.compare from, to
    end

    def get_name methdecl
      methdecl.fullname
    end

    def get_added_message methdecl
      DiffJ::TypeComparator::METHOD_ADDED
    end

    def get_removed_message methdecl
      DiffJ::TypeComparator::METHOD_REMOVED
    end
  end
end
