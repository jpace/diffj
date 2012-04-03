#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'
require 'diffj/ast/methoddecl'
require 'diffj/ast/fielddecl'
require 'diffj/ast/ctordecl'
require 'diffj/ast/innertypedecl'

include Java

import org.incava.pmdx.SimpleNodeUtil

module DiffJ
  class TypeComparator < ItemComparator
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

    def compare_extends from_type, to_type
      compare_imp_ext from_type, to_type, EXTENDED_TYPE_MSGS, "net.sourceforge.pmd.ast.ASTExtendsList"
    end

    def compare_implements from_type, to_type
      compare_imp_ext from_type, to_type, IMPLEMENTED_TYPE_MSGS, "net.sourceforge.pmd.ast.ASTImplementsList"
    end

    def get_ext_imp_map coid, ext_imp_class_name
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
    
    def compare_imp_ext from_type, to_type, msgs, ext_imp_class_name
      from_map = get_ext_imp_map from_type, ext_imp_class_name
      to_map = get_ext_imp_map to_type, ext_imp_class_name

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

    def compare_declarations from_node, to_node
      diffs = filediffs
        
      tmd = MethodDeclComparator.new diffs
      tmd.compare from_node, to_node
        
      tfd = FieldDeclComparator.new diffs
      tfd.compare from_node, to_node
        
      ctd = CtorDeclComparator.new diffs
      ctd.compare from_node, to_node
        
      titd = InnerTypeComparator.new diffs, self
      titd.compare from_node, to_node
    end

    def compare_coids from_coid, to_coid      
      if !from_coid.interface? && to_coid.interface?
        changed from_coid, to_coid, TYPE_CHANGED_FROM_CLASS_TO_INTERFACE
      elsif from_coid.interface? && !to_coid.interface?
        changed from_coid, to_coid, TYPE_CHANGED_FROM_INTERFACE_TO_CLASS
      end
        
      from_parent = SimpleNodeUtil.getParent from_coid
      to_parent = SimpleNodeUtil.getParent to_coid
      
      compare_access from_parent, to_parent
      compare_modifiers from_parent, to_parent, VALID_TYPE_MODIFIERS
      compare_extends from_coid, to_coid
      compare_implements from_coid, to_coid
      compare_declarations from_coid, to_coid
    end

    def compare from_td, to_td
      info "from_td: #{from_td}; #{from_td.class}".magenta
      info "to_td: #{to_td}; #{to_td.class}".magenta

      # class or interface declaration:
      from_type = TypeDeclarationUtil.getType from_td
      to_type = TypeDeclarationUtil.getType to_td

      if from_type && to_type
        compare_coids from_type, to_type
      end
    end
  end
end
