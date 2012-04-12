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

    def compare_init_code fromname, frominit, toname, toinit
      fromcode =  frominit.get_child_tokens
      tocode = toinit.get_child_tokens
        
      # It is logically impossible for this to execute where "to" represents the
      # from-file, and "from" the to-file, since "from.name" would have matched
      # "to.name" in the first loop of compareVariableLists
      
      compare_code fromname, fromcode, toname, tocode
    end

    def find_var_init node
      node.find_child "net.sourceforge.pmd.ast.ASTVariableInitializer"
    end

    def compare_variable_inits from, to
      frominit = find_var_init from
      toinit = find_var_init to
      
      if frominit.nil?
        if toinit
          changed from, toinit, INITIALIZER_ADDED
        end
      elsif toinit.nil?
        changed frominit, to, INITIALIZER_REMOVED
      else
        compare_init_code from.namestr, frominit, to.namestr, toinit
      end
    end

    def find_type node
      node.find_child "net.sourceforge.pmd.ast.ASTType"
    end

    def compare_variable_types name, from_field_decl, from_var_decl, to_field_decl, to_var_decl
      fromtype = find_type from_field_decl
      totype = find_type to_field_decl

      fromtypestr = fromtype.to_string
      totypestr = totype.to_string

      if fromtypestr != totypestr
        changed fromtype, totype, VARIABLE_TYPE_CHANGED, name, fromtypestr, totypestr
      end
      
      compare_variable_inits from_var_decl, to_var_decl
    end

    def process_add_del_variable name, msg, fromvardecl, tovardecl
      changed fromvardecl.nametk, tovardecl.nametk, msg, name
    end

    def process_changed_variable from_var_decl, to_var_decl
      changed from_var_decl.nametk, to_var_decl.nametk, VARIABLE_CHANGED
      compare_variable_inits from_var_decl, to_var_decl
    end
    
    def compare_variables from, to
      from_var_decls = from.find_children "net.sourceforge.pmd.ast.ASTVariableDeclarator"
      to_var_decls = to.find_children "net.sourceforge.pmd.ast.ASTVariableDeclarator"

      from_names_to_vd = make_vd_map from_var_decls
      to_names_to_vd = make_vd_map to_var_decls

      names = from_names_to_vd.keys + to_names_to_vd.keys

      names.each do |name|
        from_var_decl = from_names_to_vd[name]
        to_var_decl = to_names_to_vd[name]

        if from_var_decl && to_var_decl
          compare_variable_types name, from, from_var_decl, to, to_var_decl
        elsif from_var_decls.size == 1 && to_var_decls.size == 1
          process_changed_variable from_var_decls[0], to_var_decls[0]
        elsif from_var_decl.nil?
          process_add_del_variable name, VARIABLE_ADDED, from_var_decls[0], to_var_decl
        else
          process_add_del_variable name, VARIABLE_REMOVED, from_var_decl, to_var_decls[0]
        end
      end
    end

    def compare from, to
      compare_modifiers from, to
      compare_variables from, to
    end
  end
end
