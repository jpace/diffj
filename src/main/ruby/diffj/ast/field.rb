#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'

include Java

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
      names_to_vd = Hash.new
      vds.each do |vd|
        names_to_vd[vd.namestr] = vd
      end
      names_to_vd
    end

    def compare_init_code from_name, from_init, to_name, to_init
      from_code =  from_init.get_children_serially
      to_code = to_init.get_children_serially
        
      # It is logically impossible for this to execute where "to" represents the
      # from-file, and "from" the to-file, since "from.name" would have matched
      # "to.name" in the first loop of compareVariableLists
      
      compare_code from_name, from_code, to_name, to_code
    end

    def compare_variable_inits from, to
      from_init = from.find_child "net.sourceforge.pmd.ast.ASTVariableInitializer"
      to_init = to.find_child "net.sourceforge.pmd.ast.ASTVariableInitializer"
      
      if from_init.nil?
        if to_init
          changed from, to_init, INITIALIZER_ADDED
        end
      elsif to_init.nil?
        changed from_init, to, INITIALIZER_REMOVED
      else
        from_name = from.namestr
        to_name = to.namestr

        compare_init_code from_name, from_init, to_name, to_init
      end
    end

    def compare_variable_types name, from_field_decl, from_var_decl, to_field_decl, to_var_decl
      from_type = from_field_decl.find_child "net.sourceforge.pmd.ast.ASTType"
      to_type = to_field_decl.find_child "net.sourceforge.pmd.ast.ASTType"

      from_type_str = from_type.to_string
      to_type_str = to_type.to_string

      if from_type_str != to_type_str
        changed from_type, to_type, VARIABLE_TYPE_CHANGED, name, from_type_str, to_type_str
      end
      
      compare_variable_inits from_var_decl, to_var_decl
    end

    def process_add_del_variable name, msg, from_var_decl, to_var_decl
      from_tk = from_var_decl.nametk
      to_tk = to_var_decl.nametk
      changed from_tk, to_tk, msg, name
    end

    def process_changed_variable from_var_decl, to_var_decl
      from_tk = from_var_decl.nametk
      to_tk = to_var_decl.nametk
      changed from_tk, to_tk, VARIABLE_CHANGED
      compare_variable_inits from_var_decl, to_var_decl
    end
    
    def compare_variables from, to
      from_var_decls = from.snatch_children "net.sourceforge.pmd.ast.ASTVariableDeclarator"
      to_var_decls = to.snatch_children "net.sourceforge.pmd.ast.ASTVariableDeclarator"

      from_names_to_vd = make_vd_map from_var_decls
      to_names_to_vd = make_vd_map to_var_decls

      names = from_names_to_vd.keys + to_names_to_vd.keys

      names.each do |name|
        from_var_decl = from_names_to_vd[name]
        to_var_decl = to_names_to_vd[name]

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
