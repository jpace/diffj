#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'

include Java

import org.incava.pmdx.FieldUtil

module DiffJ
  class FieldComparator < ItemComparator
    VARIABLE_REMOVED = "variable removed: {0}"
    VARIABLE_ADDED = "variable added: {0}"
    VARIABLE_CHANGED = "variable changed from {0} to {1}"
    VARIABLE_TYPE_CHANGED = "variable type for {0} changed from {1} to {2}"
    INITIALIZER_REMOVED = "initializer removed"
    INITIALIZER_ADDED = "initializer added"

    VALID_MODIFIERS = [
                       ::Java::net.sourceforge.pmd.ast.JavaParserConstants::FINAL,
                       ::Java::net.sourceforge.pmd.ast.JavaParserConstants::STATIC,
                      ]
    
    def compare_modifiers from, to
      super from.parent, to.parent, VALID_MODIFIERS
    end

    def make_vd_map vds
      names_to_vd = java.util.HashMap.new
      vds.each do |vd|
        name = FieldUtil.getName(vd).image
        names_to_vd.put(name, vd)
      end
      names_to_vd
    end

    def compare_init_code from_name, from_init, to_name, to_init
      from_code =  from_init.get_children_serially
      to_code = SimpleNodeUtil.getChildrenSerially to_init
        
      # It is logically impossible for this to execute where "to" represents the
      # from-file, and "from" the to-file, since "from.name" would have matched
      # "to.name" in the first loop of compareVariableLists
      
      compare_code from_name, from_code, to_name, to_code
    end

    def compare_variable_inits from, to
      from_init = SimpleNodeUtil.findChild from, "net.sourceforge.pmd.ast.ASTVariableInitializer"
      to_init = SimpleNodeUtil.findChild to, "net.sourceforge.pmd.ast.ASTVariableInitializer"
      
      if from_init.nil?
        if to_init
          changed from, to_init, INITIALIZER_ADDED
        end
      elsif to_init.nil?
        changed from_init, to, INITIALIZER_REMOVED
      else
        from_name = FieldUtil.getName(from).image
        to_name = FieldUtil.getName(to).image

        compare_init_code from_name, from_init, to_name, to_init
      end
    end

    def compare_variable_types name, from_field_decl, from_var_decl, to_field_decl, to_var_decl
      from_type = SimpleNodeUtil.findChild(from_field_decl, "net.sourceforge.pmd.ast.ASTType")
      to_type = SimpleNodeUtil.findChild(to_field_decl, "net.sourceforge.pmd.ast.ASTType")

      from_type_str = SimpleNodeUtil.toString from_type
      to_type_str = SimpleNodeUtil.toString to_type

      if from_type_str != to_type_str
        changed from_type, to_type, VARIABLE_TYPE_CHANGED, name, from_type_str, to_type_str
      end
      
      compare_variable_inits from_var_decl, to_var_decl
    end

    def process_add_del_variable name, msg, from_var_decl, to_var_decl
      from_tk = FieldUtil.getName from_var_decl
      to_tk = FieldUtil.getName to_var_decl
      changed from_tk, to_tk, msg, name
    end

    def process_changed_variable from_var_decl, to_var_decl
      from_tk = FieldUtil.getName from_var_decl
      to_tk = FieldUtil.getName to_var_decl
      changed from_tk, to_tk, VARIABLE_CHANGED
      compare_variable_inits from_var_decl, to_var_decl
    end
    
    def compare_variables from, to
      from_var_decls = SimpleNodeUtil.snatchChildren(from, "net.sourceforge.pmd.ast.ASTVariableDeclarator")
      to_var_decls = SimpleNodeUtil.snatchChildren(to, "net.sourceforge.pmd.ast.ASTVariableDeclarator")

      from_names_to_vd = make_vd_map from_var_decls
      to_names_to_vd = make_vd_map to_var_decls

      names = java.util.TreeSet.new
      names.addAll(from_names_to_vd.keySet())
      names.addAll(to_names_to_vd.keySet())

      names.each do |name|
        from_var_decl = from_names_to_vd.get(name)
        to_var_decl = to_names_to_vd.get(name)

        if from_var_decl && to_var_decl
          compare_variable_types name, from, from_var_decl, to, to_var_decl
        elsif from_var_decls.size == 1 && to_var_decls.size == 1
          process_changed_variable from_var_decls.get(0), to_var_decls.get(0)
        elsif from_var_decl.nil?
          process_add_del_variable name, VARIABLE_ADDED, from_var_decls.get(0), to_var_decl
        else
          process_add_del_variable name, VARIABLE_REMOVED, from_var_decl, to_var_decls.get(0)
        end
      end
    end

    def compare from, to
      compare_modifiers from, to
      compare_variables from, to
    end
  end
end
