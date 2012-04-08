#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/element'

include Java

module DiffJ
  class ItemComparator < ElementComparator
    include Loggable

    MODIFIER_REMOVED = "modifier removed: {0}"
    MODIFIER_ADDED = "modifier added: {0}"
    MODIFIER_CHANGED = "modifier changed from {0} to {1}"

    ACCESS_REMOVED = "access removed: {0}"
    ACCESS_ADDED = "access added: {0}"
    ACCESS_CHANGED = "access changed from {0} to {1}"
    
    CODE_CHANGED = "code changed in {0}"
    CODE_ADDED = "code added in {0}"
    CODE_REMOVED = "code removed in {0}"

    def initialize diffs
      super diffs
    end

    def get_modifier_map node
      bykind = Hash.new
      tokens = org.incava.pmdx.SimpleNodeUtil.getLeadingTokens node
      tokens.each do |tk|
        bykind[tk.kind] = tk
      end
      bykind
    end

    def compare_modifiers from_node, to_node, modifier_types
      from_modifiers = org.incava.pmdx.SimpleNodeUtil.getLeadingTokens from_node
      to_modifiers = org.incava.pmdx.SimpleNodeUtil.getLeadingTokens to_node

      from_kind_to_token = get_modifier_map from_node
      to_kind_to_token = get_modifier_map to_node

      modifier_types.each do |modkind|
        from_mod = from_kind_to_token[modkind]
        to_mod = to_kind_to_token[modkind]

        if from_mod
          if to_mod.nil?
            changed from_mod, to_node.first_token, MODIFIER_REMOVED, from_mod.image
          end
        elsif to_mod
          changed from_node.first_token, to_mod, MODIFIER_ADDED, to_mod.image
        end
      end
    end

    def compare_access from_node, to_node
      from_access = org.incava.pmdx.ItemUtil.getAccess from_node
      to_access = org.incava.pmdx.ItemUtil.getAccess to_node

      if from_access
        if to_access
          if from_access.image != to_access.image
            changed from_access, to_access, ACCESS_CHANGED, from_access.image, to_access.image
          end
        else
          changed from_access, to_node.first_token, ACCESS_REMOVED, from_access.image
        end
      elsif to_access
        changed from_node.first_token, to_access, ACCESS_ADDED, to_access.image
      end
    end

    def get_start token_list, start
      sttoken = org.incava.ijdk.util.ListExt.get token_list, start
      if sttoken.nil? && list.size() > 0
        sttoken = org.incava.ijdk.util.ListExt.get token_list, -1
        sttoken = sttoken.next
      end
      sttoken
    end

    def get_message add_end, del_end
      del_end == org.incava.ijdk.util.diff.Difference::NONE ? CODE_ADDED : (add_end == org.incava.ijdk.util.diff.Difference::NONE ? CODE_REMOVED : CODE_CHANGED)
    end

    def get_location_range token_list, startidx, endidx
      starttk = nil
      endtk = nil
      if endidx == org.incava.ijdk.util.diff.Difference::NONE
        starttk = get_start token_list, startidx
        endtk = starttk
      else
        starttk = token_list.get startidx
        endtk = token_list.get endidx
      end
     org.incava.ijdk.text.LocationRange.new org.incava.analysis.FileDiff.toBeginLocation(starttk), org.incava.analysis.FileDiff.toEndLocation(endtk)
    end

    def on_same_line? ref, locrg
      ref && ref.getFirstLocation().getStart().getLine() == locrg.getStart().getLine()
    end

    def replace_reference name, ref, from_loc_rg, to_loc_rg
      new_msg  = java.text.MessageFormat.format CODE_CHANGED, name
      new_diff = org.incava.analysis.FileDiffChange.new(new_msg, ref.getFirstLocation().getStart(), from_loc_rg.getEnd(), ref.getSecondLocation().getStart(), to_loc_rg.getEnd())
      getFileDiffs().remove(ref)
      add(new_diff)
      new_diff
    end

    def add_reference name, msg, from_loc_rg, to_loc_rg
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
    
    def process_difference diff, from_name, from_list, to_list, prev_ref
      del_start = diff.getDeletedStart()
      del_end   = diff.getDeletedEnd()
      add_start = diff.getAddedStart()
      add_end   = diff.getAddedEnd()
      
      if del_end == org.incava.ijdk.util.diff.Difference::NONE && add_end == org.incava.ijdk.util.diff.Difference::NONE
        # WTF?
        return nil
      end

      from_loc_rg = get_location_range from_list, del_start, del_end
      to_loc_rg = get_location_range to_list, add_start, add_end

      msg = get_message add_end, del_end
      info "msg: #{msg}".on_green
            
      # $$$ this is untested:
      if on_same_line? prev_ref, from_loc_rg
        info "self: #{self}".yellow
        replace_reference from_name, prev_ref, from_loc_rg, to_loc_rg
      else
        info "self: #{self}".blue
        ref = add_reference from_name, msg, from_loc_rg, to_loc_rg
        info "ref: #{ref}".blue
        ref
      end
    end

    def compare_code from_name, from_list, to_name, to_list
      info "self: #{self}".on_cyan
      tc = org.incava.diffj.ItemDiff::TokenComparator.new
      d = org.incava.ijdk.util.diff.Diff.new from_list, to_list, tc
        
      ref = nil
      difflist = d.diff
      
      difflist.each do |diff|
        info "diff: #{diff}".red
        ref = process_difference diff, from_name, from_list, to_list, ref
        return if ref.nil?
      end
    end
  end
end
