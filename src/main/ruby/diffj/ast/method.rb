#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'
require 'diffj/ast/function'

include Java

import org.incava.diffj.MethodDiff
import org.incava.pmdx.ParameterUtil
import org.incava.pmdx.MethodUtil

module DiffJ
  class MethodComparator < MethodDiff
    include Loggable, FunctionComparator

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

      # fake superclass, for now:
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

    def method_compare_parameters_xxx from, to
      from_params = MethodUtil.getParameters from
      to_params = MethodUtil.getParameters to
      
      # should be calling super:
      function_compare_parameters_xxx from_params, to_params
    end

    def compare_xxx from, to
      info "from: #{from}".on_red
      info "to  : #{to}".on_red

      compare_modifiers_xxx from, to
      compare_return_types_xxx from, to
      method_compare_parameters_xxx from, to

      compareThrows(from, to)
      compareBodies(from, to)
    end
  end
end
