#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'

include Java

import org.incava.diffj.MethodDiff

module DiffJ
  class MethodComparator < MethodDiff
    include Loggable

    METHOD_BLOCK_ADDED = "method block added"
    METHOD_BLOCK_REMOVED = "method block removed"

    VALID_MODIFIERS = [
      ::Java::net.sourceforge.pmd.ast.JavaParserConstants::ABSTRACT,
      ::Java::net.sourceforge.pmd.ast.JavaParserConstants::FINAL,
      ::Java::net.sourceforge.pmd.ast.JavaParserConstants::NATIVE,
      ::Java::net.sourceforge.pmd.ast.JavaParserConstants::STATIC,
      ::Java::net.sourceforge.pmd.ast.JavaParserConstants::STRICTFP
    ]
    
    def initialize diffs
      super
      @itemcomp = ItemComparator.new diffs
    end

    def compare_access_xxx from, to
      info "from: #{from}".on_red
      info "to  : #{to}".on_red

      @itemcomp.compare_access from, to
    end

    def compare_modifiers_xxx from, to
      @itemcomp.compare_modifiers SimpleNodeUtil.getParent(from), SimpleNodeUtil.getParent(to), VALID_MODIFIERS
    end

    def function_compare_return_types_xxx from, to
      from_ret_type     = from.jjtGetChild(0)
      to_ret_type       = to.jjtGetChild(0)
      from_ret_type_str = SimpleNodeUtil.toString from_ret_type
      to_ret_type_str   = SimpleNodeUtil.toString to_ret_type

      if from_ret_type_str != to_ret_type_str
        changed from_ret_type, to_ret_type, RETURN_TYPE_CHANGED, from_ret_type_str, to_ret_type_str
      end
    end

    def compare_xxx from, to
      info "from: #{from}".on_red
      info "to  : #{to}".on_red

      compare_modifiers_xxx from, to
      function_compare_return_types_xxx from, to

      compareParameters(from, to)
      compareThrows(from, to)
      compareBodies(from, to)
    end
  end
end
