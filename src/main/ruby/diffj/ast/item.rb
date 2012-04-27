#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/element'
require 'diffj/util/diff/lcs'
require 'diffj/util/diff/traverser'
require 'diffj/util/diff/delta'

include Java
include DiffJ::IO

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
      tokens = node.leading_tokens
      tokens.each do |tk|
        bykind[tk.kind] = tk
      end
      bykind
    end

    def compare_modifiers from_node, to_node, modifier_types
      from_modifiers = from_node.leading_tokens 
      to_modifiers = to_node.leading_tokens 

      from_kind_to_token = get_modifier_map from_node
      to_kind_to_token = get_modifier_map to_node

      modifier_types.each do |modkind|
        frommod = from_kind_to_token[modkind]
        tomod = to_kind_to_token[modkind]

        if frommod
          if tomod.nil?
            changed frommod, to_node.token(0), MODIFIER_REMOVED, frommod.image
          end
        elsif tomod
          changed from_node.token(0), tomod, MODIFIER_ADDED, tomod.image
        end
      end
    end

    def compare_access from_node, to_node
      from_access = from_node.accesstk
      to_access = to_node.accesstk

      if from_access
        if to_access
          if from_access.image != to_access.image
            changed from_access, to_access, ACCESS_CHANGED, from_access.image, to_access.image
          end
        else
          changed from_access, to_node.token(0), ACCESS_REMOVED, from_access.image
        end
      elsif to_access
        changed from_node.token(0), to_access, ACCESS_ADDED, to_access.image
      end
    end

    def get_start token_list, start
      sttoken = token_list[start]
      if sttoken.nil? && !token_list.empty?
        sttoken = token_list[-1]
        sttoken = sttoken.next
      end
      sttoken
    end

    def get_message addend, delend
      is_none?(delend) ? CODE_ADDED : (is_none?(addend) ? CODE_REMOVED : CODE_CHANGED)
    end

    def is_none? idx
      idx.nil? || idx == org.incava.ijdk.util.diff.Difference::NONE
    end

    def get_location_range tokenlist, startidx, endidx
      starttk = nil
      endtk = nil
      if is_none? endidx
        starttk = get_start tokenlist, startidx
        endtk = starttk
      else
        starttk = tokenlist[startidx]
        endtk = tokenlist[endidx]
      end

      from = Location.beginning starttk
      to = Location.ending endtk

      LocationRange.new from, to
    end

    def on_same_line? ref, locrg
      ref && ref.first_location.from.line == locrg.from.line
    end

    def replace_reference name, ref, fromlocrg, tolocrg
      newmsg  = ResourceString.new(CODE_CHANGED).format name
      locs    = [ ref.first_location.from, fromlocrg.to, ref.second_location.from, tolocrg.to ]
      newdiff = DiffJ::FDiffChange.new newmsg, :locations => locs

      filediffs.delete ref
      add newdiff
      newdiff
    end

    def add_reference name, msg, fromlocrg, tolocrg
      str = ResourceString.new(msg).format name
      fdiffcls = case msg
                 when CODE_ADDED
                   # this will show as add when highlighted, as change when not.
                   DiffJ::FDiffCodeAdded
                 when CODE_REMOVED
                   DiffJ::FDiffCodeDeleted
                 else
                   DiffJ::FDiffChange
                 end
      
      ref = fdiffcls.new str, :locranges => [ fromlocrg, tolocrg ]
      add ref
      ref
    end

    def is_diff? diff
      diff != org.incava.ijdk.util.diff.Difference::NONE
    end
    
    def javadiff_process_difference diff, from_name, from_list, to_list, prev_ref
      delstart = diff.getDeletedStart()
      delend   = diff.getDeletedEnd()
      addstart = diff.getAddedStart()
      addend   = diff.getAddedEnd()
      
      # I have this guard here, but I don't know that it's ever been hit
      return nil if is_none?(delend) && is_none?(addend)
      
      fromlocrg = get_location_range from_list, delstart, delend
      tolocrg = get_location_range to_list, addstart, addend

      msg = get_message addend, delend
            
      # $$$ this is untested:
      if on_same_line? prev_ref, fromlocrg
        replace_reference from_name, prev_ref, fromlocrg, tolocrg
      else
        add_reference from_name, msg, fromlocrg, tolocrg
      end
    end
    
    def jrubydiff_process_difference delta, from_name, from_list, to_list, prev_ref
      delstart = delta.delete_start
      delend   = delta.delete_end
      addstart = delta.add_start
      addend   = delta.add_end
      
      # I have this guard here, but I don't know that it's ever been hit
      return nil if is_none?(delend) && is_none?(addend)
      
      fromlocrg = get_location_range from_list, delstart, delend
      tolocrg = get_location_range to_list, addstart, addend

      msg = get_message addend, delend
      info "msg: #{msg}"
            
      # $$$ this is untested:
      if on_same_line? prev_ref, fromlocrg
        info "self: #{self}"
        replace_reference from_name, prev_ref, fromlocrg, tolocrg
      else
        info "self: #{self}"
        ref = add_reference from_name, msg, fromlocrg, tolocrg
        info "ref: #{ref}".bold.blue
        ref
      end
    end

    def jrubydiff_compare_code from_name, from_list, to_name, to_list
      matches = DiffJ::DiffLCS::LCS.new(from_list, to_list).matches
      trav = DiffJ::DiffLCS::Traverser.new matches, from_list.size, to_list.size

      ref = nil      
      difflist = trav.diffs
      
      difflist.each do |diff|
        info "diff: #{diff}"
        ref = jrubydiff_process_difference diff, from_name, from_list, to_list, ref
        return if ref.nil?
      end
    end

    def javadiff_compare_code from_name, from_list, to_name, to_list
      info "self: #{self}".bold.red.on_cyan

      tc = org.incava.diffj.ItemDiff::TokenComparator.new
      d = org.incava.ijdk.util.diff.Diff.new from_list, to_list, tc
        
      ref = nil
      difflist = d.diff
      
      difflist.each do |diff|
        info "diff: #{diff}".red
        ref = javadiff_process_difference diff, from_name, from_list, to_list, ref
        return if ref.nil?
      end
    end

    def compare_code from_name, from_list, to_name, to_list
      jrubydiff_compare_code from_name, from_list, to_name, to_list
      # javadiff_compare_code from_name, from_list, to_name, to_list
    end
  end
end
