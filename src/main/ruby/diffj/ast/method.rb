#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'
require 'diffj/ast/item'
require 'diffj/ast/function'

module DiffJ
  class MethodComparator < FunctionComparator

    METHOD_BLOCK_ADDED = "method block added"
    METHOD_BLOCK_REMOVED = "method block removed"

    VALID_MODIFIERS = [
                       ::Java::net.sourceforge.pmd.ast.JavaParserConstants::ABSTRACT,
                       ::Java::net.sourceforge.pmd.ast.JavaParserConstants::FINAL,
                       ::Java::net.sourceforge.pmd.ast.JavaParserConstants::NATIVE,
                       ::Java::net.sourceforge.pmd.ast.JavaParserConstants::STATIC,
                       ::Java::net.sourceforge.pmd.ast.JavaParserConstants::STRICTFP
                      ]
    
    def compare_modifiers from, to
      super from.parent, to.parent, VALID_MODIFIERS
    end

    def compare_parameters from, to
      super from.parameters, to.parameters
    end

    def compare_throws from, to
      super from, from.throws_list, to, to.throws_list
    end

    def get_block node
      node.find_child "net.sourceforge.pmd.ast.ASTBlock"
    end
    
    def compare_bodies from, to
      fromblock = get_block from
      toblock = get_block to

      if fromblock.nil?
        if toblock
          changed from, to, METHOD_BLOCK_ADDED
        end
      elsif toblock.nil?
        changed from, to, METHOD_BLOCK_REMOVED
      else
        compare_blocks from.fullname, fromblock, to.fullname, toblock
      end
    end

    def compare_blocks fromname, fromblock, toname, toblock
      fromcode = fromblock.get_child_tokens
      tocode = toblock.get_child_tokens

      compare_code fromname, fromcode, toname, tocode
    end

    def compare from, to
      compare_modifiers from, to
      compare_return_types from, to
      compare_parameters from, to

      compare_throws from, to
      compare_bodies from, to
    end
  end
end
