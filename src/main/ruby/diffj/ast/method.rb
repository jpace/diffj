#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'
require 'diffj/ast/function'

include Java

import org.incava.pmdx.MethodUtil

module DiffJ
  class MethodComparator < FunctionComparator
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
    end

    def compare_modifiers from, to
      super from.parent, to.parent, VALID_MODIFIERS
    end

    def compare_parameters from, to
      from_params = MethodUtil.getParameters from
      to_params = MethodUtil.getParameters to
      
      super from_params, to_params
    end

    def compare_throws from, to
      from_list = MethodUtil.getThrowsList from
      to_list = MethodUtil.getThrowsList to

      super from, from_list, to, to_list
    end

    def get_block node
      SimpleNodeUtil.findChild node, "net.sourceforge.pmd.ast.ASTBlock"
    end
    
    def compare_bodies from, to
      from_block = get_block from
      to_block = get_block to

      if from_block.nil?
        if to_block
          changed from, to, METHOD_BLOCK_ADDED
        end
      elsif to_block.nil?
        changed from, to, METHOD_BLOCK_REMOVED
      else
        from_name = MethodUtil.getFullName from
        to_name = MethodUtil.getFullName to
            
        compare_blocks from_name, from_block, to_name, to_block
      end
    end

    def compare_blocks from_name, from_block, to_name, to_block
      from_code = from_block.get_children_serially
      to_code = to_block.get_children_serially

      compare_code from_name, from_code, to_name, to_code
    end

    def compare from, to
      info "from: #{from}".on_red
      info "to  : #{to}".on_red

      compare_modifiers from, to
      compare_return_types from, to
      compare_parameters from, to

      compare_throws from, to
      compare_bodies from, to
    end
  end
end
