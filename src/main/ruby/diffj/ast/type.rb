#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/methoddecl'

include Java

import org.incava.diffj.DiffComparator
import org.incava.diffj.ItemDiff
import org.incava.diffj.TypeCtorDiff
import org.incava.diffj.TypeDiff
import org.incava.diffj.TypeFieldDiff
import org.incava.diffj.TypeInnerTypeDiff
import org.incava.diffj.TypeMethodDiff
import org.incava.pmdx.ItemUtil
import org.incava.pmdx.SimpleNodeUtil

module DiffJ
  class TypeComparator < TypeDiff
    include Loggable

    TYPE_CHANGED_FROM_CLASS_TO_INTERFACE = "type changed from class to interface"
    TYPE_CHANGED_FROM_INTERFACE_TO_CLASS = "type changed from interface to class"

    METHOD_REMOVED = "method removed: {0}"
    METHOD_ADDED = "method added: {0}"
    METHOD_CHANGED = "method changed from {0} to {1}"
    METHOD_MSGS = [ METHOD_ADDED, METHOD_REMOVED, METHOD_CHANGED ]

    CONSTRUCTOR_REMOVED = "constructor removed: {0}"
    CONSTRUCTOR_ADDED = "constructor added: {0}"

    FIELD_REMOVED = "field removed: {0}"
    FIELD_ADDED = "field added: {0}"

    INNER_INTERFACE_ADDED = "inner interface added: {0}"
    INNER_INTERFACE_REMOVED = "inner interface removed: {0}"

    INNER_CLASS_ADDED = "inner class added: {0}"
    INNER_CLASS_REMOVED = "inner class removed: {0}"
    INNER_CLASS_MSGS = [ INNER_CLASS_ADDED, INNER_CLASS_REMOVED ]

    EXTENDED_TYPE_REMOVED = "extended type removed: {0}"
    EXTENDED_TYPE_ADDED = "extended type added: {0}"
    EXTENDED_TYPE_CHANGED = "extended type changed from {0} to {1}"
    EXTENDED_TYPE_MSGS = [ EXTENDED_TYPE_ADDED, EXTENDED_TYPE_REMOVED, EXTENDED_TYPE_CHANGED ]

    IMPLEMENTED_TYPE_REMOVED = "implemented type removed: {0}"
    IMPLEMENTED_TYPE_ADDED = "implemented type added: {0}"
    IMPLEMENTED_TYPE_CHANGED = "implemented type changed from {0} to {1}"
    IMPLEMENTED_TYPE_MSGS = [ IMPLEMENTED_TYPE_ADDED, IMPLEMENTED_TYPE_REMOVED, IMPLEMENTED_TYPE_CHANGED ]

    VALID_TYPE_MODIFIERS = [
                            ::Java::net.sourceforge.pmd.ast.JavaParserConstants.ABSTRACT,
                            ::Java::net.sourceforge.pmd.ast.JavaParserConstants.FINAL,
                            ::Java::net.sourceforge.pmd.ast.JavaParserConstants.STATIC, # valid only for inner types
                            ::Java::net.sourceforge.pmd.ast.JavaParserConstants.STRICTFP
                           ]

    def initialize diffs
      super diffs
    end

    # this should go into ItemComparator:
    def compare_access_xxx from_node, to_node
      from_access = ItemUtil.getAccess from_node
      to_access = ItemUtil.getAccess to_node

      if from_access
        if to_access
          if from_access.image != to_access.image
            changed from_access, to_access, ItemDiff::ACCESS_CHANGED, from_access.image, to_access.image
          end
        else
          changed from_access, to_node.first_token, ItemDiff::ACCESS_REMOVED, from_access.image
        end
      elsif to_access
        changed from_node.first_token, to_access, ItemDiff::ACCESS_ADDED, to_access.image
      end
    end
    
    def get_modifier_map_xxx node
      bykind = Hash.new
      tokens = SimpleNodeUtil.getLeadingTokens node
      tokens.each do |tk|
        bykind[tk.kind] = tk
      end
      bykind
    end

    def compare_modifiers_xxx from_node, to_node, modifier_types
      from_modifiers = SimpleNodeUtil.getLeadingTokens from_node
      to_modifiers = SimpleNodeUtil.getLeadingTokens to_node

      from_kind_to_token = get_modifier_map_xxx from_node
      to_kind_to_token = get_modifier_map_xxx to_node

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

    def compare_extends_xxx from_type, to_type
      compare_imp_ext_xxx from_type, to_type, EXTENDED_TYPE_MSGS, "net.sourceforge.pmd.ast.ASTExtendsList"
    end

    def compare_implements_xxx from_type, to_type
      compare_imp_ext_xxx from_type, to_type, IMPLEMENTED_TYPE_MSGS, "net.sourceforge.pmd.ast.ASTImplementsList"
    end

    def get_ext_imp_map_xxx coid, ext_imp_class_name
      map = Hash.new
      list = SimpleNodeUtil.findChild coid, ext_imp_class_name
      
      if list
        types = java.util.ArrayList.new
        SimpleNodeUtil.fetchChildren types, list, "net.sourceforge.pmd.ast.ASTClassOrInterfaceType"
        types.each do |type|
          map[SimpleNodeUtil.toString(type)] = type
        end
      end
      map
    end
    
    def compare_imp_ext_xxx from_type, to_type, msgs, ext_imp_class_name
      from_map = get_ext_imp_map_xxx from_type, ext_imp_class_name
      to_map = get_ext_imp_map_xxx to_type, ext_imp_class_name

      # change from x to y, instead of "add x, remove y"
      
      if from_map.length == 1 && to_map.length == 1
        from_name = from_map.keys[0]
        to_name = to_map.keys[0]

        if from_name != to_name
          from = from_map[from_name]
          to = to_map[to_name]
                
          changed from, to, msgs[2], from_name, to_name
        end
      else
        type_names = from_map.keys + to_map.keys

        type_names.each do |type_name|
          from = from_map[type_name]
          to = to_map[type_name]

          if from.nil?
            changed from_type, to, msgs[0], type_name
          elsif to.nil?
            changed from, to_type, msgs[1], type_name
          end
        end
      end
    end

    def compare_declarations_xxx from_node, to_node
      diffs = getFileDiffs
        
      tmd = MethodDeclComparator.new diffs
      tmd.compare_xxx from_node, to_node
        
      tfd = TypeFieldDiff.new diffs
      tfd.compare from_node, to_node
        
      ctd = TypeCtorDiff.new diffs
      ctd.compare from_node, to_node
        
      titd = TypeInnerTypeDiff.new diffs, self
      titd.compare from_node, to_node
    end

    def compare_xxx from_td, to_td
      # class or interface declaration:
      from_type = TypeDeclarationUtil.getType from_td
      to_type = TypeDeclarationUtil.getType to_td

      return unless from_type && to_type
      
      if !from_type.interface? && to_type.interface?
        changed from_type, to_type, TYPE_CHANGED_FROM_CLASS_TO_INTERFACE
      elsif from_type.interface? && !to_type.interface?
        changed from_type, to_type, TYPE_CHANGED_FROM_INTERFACE_TO_CLASS
      end
        
      from_parent = SimpleNodeUtil.getParent from_type
      to_parent = SimpleNodeUtil.getParent to_type
      
      compare_access_xxx from_parent, to_parent
      compare_modifiers_xxx from_parent, to_parent, VALID_TYPE_MODIFIERS
      compare_extends_xxx from_type, to_type
      compare_implements_xxx from_type, to_type
      compare_declarations_xxx from_type, to_type
    end
  end
end
