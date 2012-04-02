#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

import org.incava.diffj.DiffComparator
import org.incava.pmdx.ItemUtil
import org.incava.pmdx.SimpleNodeUtil

module DiffJ
  class ItemComparator < DiffComparator
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
      tokens = SimpleNodeUtil.getLeadingTokens node
      tokens.each do |tk|
        bykind[tk.kind] = tk
      end
      bykind
    end

    def compare_modifiers from_node, to_node, modifier_types
      from_modifiers = SimpleNodeUtil.getLeadingTokens from_node
      to_modifiers = SimpleNodeUtil.getLeadingTokens to_node

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
      from_access = ItemUtil.getAccess from_node
      to_access = ItemUtil.getAccess to_node

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
  end
end
