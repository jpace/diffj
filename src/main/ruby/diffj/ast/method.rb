#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'
require 'diffj/ast/function'

include Java

import org.incava.analysis.FileDiff
import org.incava.diffj.ItemDiff
import org.incava.diffj.MethodDiff
import org.incava.ijdk.text.LocationRange
import org.incava.pmdx.MethodUtil
import org.incava.pmdx.ParameterUtil

module DiffJ
  class MethodComparator < FunctionComparator # < MethodDiff
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

      # fake superclass, for now:
      @itemcomp = ItemComparator.new diffs
    end

    def compare_access_xxx from, to
      info "from: #{from}"
      info "to  : #{to}"
      @itemcomp.compare_access from, to
    end

    def compare_modifiers_xxx from, to
      @itemcomp.compare_modifiers from.parent, to.parent, VALID_MODIFIERS
    end

    def method_compare_parameters_xxx from, to
      from_params = MethodUtil.getParameters from
      to_params = MethodUtil.getParameters to
      
      # should be calling super:
      function_compare_parameters_xxx from_params, to_params
    end

    def method_compare_throws_xxx from, to
      from_list = MethodUtil.getThrowsList from
      to_list = MethodUtil.getThrowsList to

      function_compare_throws_xxx from, from_list, to, to_list
    end

    def method_get_block node
      SimpleNodeUtil.findChild node, "net.sourceforge.pmd.ast.ASTBlock"
    end
    
    def method_compare_bodies_xxx from, to
      from_block = method_get_block from
      to_block = method_get_block to

      if from_block.nil?
        if to_block
          changed from, to, METHOD_BLOCK_ADDED
        end
      elsif to_block.nil?
        changed from, to, METHOD_BLOCK_REMOVED
      else
        from_name = MethodUtil.getFullName from
        to_name = MethodUtil.getFullName to
            
        method_compare_blocks_xxx from_name, from_block, to_name, to_block
      end
    end

    def item_get_start_xxx token_list, start
      sttoken = org.incava.ijdk.util.ListExt.get token_list, start
      if sttoken.nil? && list.size() > 0
        sttoken = org.incava.ijdk.util.ListExt.get token_list, -1
        sttoken = sttoken.next
      end
      sttoken
    end

    def item_get_message add_end, del_end
      del_end == org.incava.ijdk.util.diff.Difference::NONE ? CODE_ADDED : (add_end == org.incava.ijdk.util.diff.Difference::NONE ? CODE_REMOVED : CODE_CHANGED)
    end

    def item_get_location_range_xxx token_list, startidx, endidx
      starttk = nil
      endtk = nil
      if endidx == org.incava.ijdk.util.diff.Difference::NONE
        starttk = item_get_start_xxx token_list, startidx
        endtk = starttk
      else
        starttk = token_list.get startidx
        endtk = token_list.get endidx
      end
     LocationRange.new FileDiff.toBeginLocation(starttk), FileDiff.toEndLocation(endtk)
    end

    def item_on_same_line? ref, locrg
      ref && ref.getFirstLocation().getStart().getLine() == locrg.getStart().getLine()
    end

    def item_replace_reference_xxx name, ref, from_loc_rg, to_loc_rg
      new_msg  = java.text.MessageFormat.format CODE_CHANGED, name
      new_diff = org.incava.analysis.FileDiffChange.new(new_msg, ref.getFirstLocation().getStart(), from_loc_rg.getEnd(), ref.getSecondLocation().getStart(), to_loc_rg.getEnd())
      getFileDiffs().remove(ref)
      add(new_diff)
      new_diff
    end

    def item_add_reference_xxx name, msg, from_loc_rg, to_loc_rg
      str = java.text.MessageFormat.format msg, name
      ref = case msg
            when CODE_ADDED
              # this will show as add when highlighted, as change when not.
              org.incava.analysis.FileDiffCodeAdded.new str, from_loc_rg, to_loc_rg
            when CODE_REMOVED
              org.incava.analysis.FileDiffCodeDeleted.new str, from_loc_rg, to_loc_rg
            else
              org.incava.analysis.FileDiffChange.new str, from_loc_rg, to_loc_rg
            end
      add ref
      ref
    end
    
    def item_process_difference_xxx diff, from_name, from_list, to_list, prev_ref
      del_start = diff.getDeletedStart()
      del_end   = diff.getDeletedEnd()
      add_start = diff.getAddedStart()
      add_end   = diff.getAddedEnd()
      
      if del_end == org.incava.ijdk.util.diff.Difference::NONE && add_end == org.incava.ijdk.util.diff.Difference::NONE
        # WTF?
        return nil
      end

      from_loc_rg = item_get_location_range_xxx from_list, del_start, del_end
      to_loc_rg = item_get_location_range_xxx to_list, add_start, add_end

      msg = item_get_message add_end, del_end
      info "msg: #{msg}".on_green
            
      # $$$ this is untested:
      if item_on_same_line? prev_ref, from_loc_rg
        info "self: #{self}".yellow
        item_replace_reference_xxx from_name, prev_ref, from_loc_rg, to_loc_rg
      else
        info "self: #{self}".blue
        ref = item_add_reference_xxx from_name, msg, from_loc_rg, to_loc_rg
        info "ref: #{ref}".blue
        ref
      end
    end

    def item_compare_code_xxx from_name, from_list, to_name, to_list
      info "self: #{self}".on_cyan
      tc = org.incava.diffj.ItemDiff::TokenComparator.new
      d = org.incava.ijdk.util.diff.Diff.new from_list, to_list, tc
        
      ref = nil
      difflist = d.diff
      
      difflist.each do |diff|
        info "diff: #{diff}".red
        ref = item_process_difference_xxx diff, from_name, from_list, to_list, ref
        return if ref.nil?
      end
    end

    def method_compare_blocks_xxx from_name, from_block, to_name, to_block
      from_code = from_block.get_children_serially
      to_code = to_block.get_children_serially

      item_compare_code_xxx from_name, from_code, to_name, to_code
    end

    def compare_xxx from, to
      info "from: #{from}".on_red
      info "to  : #{to}".on_red

      compare_modifiers_xxx from, to
      compare_return_types_xxx from, to
      method_compare_parameters_xxx from, to

      method_compare_throws_xxx from, to
      method_compare_bodies_xxx from, to
    end
  end
end
