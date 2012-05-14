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

module DiffJ
  module TypeMessages
    EXTENDED_TYPE_REMOVED = "extended type removed: {0}"
    EXTENDED_TYPE_ADDED = "extended type added: {0}"
    EXTENDED_TYPE_CHANGED = "extended type changed from {0} to {1}"
    EXTENDED_TYPE_MSGS = [ EXTENDED_TYPE_ADDED, EXTENDED_TYPE_REMOVED, EXTENDED_TYPE_CHANGED ]

    IMPLEMENTED_TYPE_REMOVED = "implemented type removed: {0}"
    IMPLEMENTED_TYPE_ADDED = "implemented type added: {0}"
    IMPLEMENTED_TYPE_CHANGED = "implemented type changed from {0} to {1}"
    IMPLEMENTED_TYPE_MSGS = [ IMPLEMENTED_TYPE_ADDED, IMPLEMENTED_TYPE_REMOVED, IMPLEMENTED_TYPE_CHANGED ]
  end

  class SupertypeComparator < ElementComparator    
    include TypeMessages

    def initialize filediffs, from_type, to_type
      super filediffs
      compare from_type, to_type
    end

    def get_supertype_map coid
      map = Hash.new
      if list = coid.find_child(ast_type)
        types = list.find_children "net.sourceforge.pmd.ast.ASTClassOrInterfaceType"
        types.each do |type|
          map[type.to_string] = type
        end
      end
      map
    end
    
    def compare from_type, to_type
      msgs = messages
      from_map = get_supertype_map from_type
      to_map   = get_supertype_map to_type

      # change from x to y, instead of "add x, remove y"
      
      if from_map.length == 1 && to_map.length == 1
        from_name = from_map.keys[0]
        to_name = to_map.keys[0]

        if from_name != to_name
          from = from_map[from_name]
          to = to_map[to_name]
                
          changed from, to, messages[2], from_name, to_name
        end
      else
        type_names = from_map.keys + to_map.keys

        type_names.each do |type_name|
          from = from_map[type_name]
          to = to_map[type_name]

          if from.nil?
            stlist = from_type.find_child(ast_type) || from_type
            added stlist, to, messages[0], type_name
          elsif to.nil?
            stlist = to_type.find_child(ast_type) || to_type
            deleted from, stlist, messages[1], type_name
          end
        end
      end
    end
    
  end

  class ImplementsComparator < SupertypeComparator
    def messages 
      IMPLEMENTED_TYPE_MSGS
    end

    def ast_type
      "net.sourceforge.pmd.ast.ASTImplementsList"
    end
  end

  class ExtendsComparator < SupertypeComparator
    def messages 
      EXTENDED_TYPE_MSGS
    end

    def ast_type
      "net.sourceforge.pmd.ast.ASTExtendsList"
    end
  end

  class TypeComparator < ItemComparator
    include Loggable, TypeMessages

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

    VALID_TYPE_MODIFIERS = [
                            ::Java::net.sourceforge.pmd.ast.JavaParserConstants.ABSTRACT,
                            ::Java::net.sourceforge.pmd.ast.JavaParserConstants.FINAL,
                            ::Java::net.sourceforge.pmd.ast.JavaParserConstants.STATIC, # valid only for inner types
                            ::Java::net.sourceforge.pmd.ast.JavaParserConstants.STRICTFP
                           ]

    def compare_extends from_type, to_type
      ExtendsComparator.new filediffs, from_type, to_type
    end

    def compare_implements from_type, to_type
      ImplementsComparator.new filediffs, from_type, to_type
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
        
      from_parent = from_coid.parent
      to_parent = to_coid.parent
      
      compare_access from_parent, to_parent
      compare_modifiers from_parent, to_parent, VALID_TYPE_MODIFIERS
      compare_extends from_coid, to_coid
      compare_implements from_coid, to_coid
      compare_declarations from_coid, to_coid
    end

    def compare from_td, to_td
      # class or interface declaration:
      fromtype = from_td.type_node
      totype = to_td.type_node

      if fromtype && totype
        compare_coids fromtype, totype
      end
    end
  end
end
