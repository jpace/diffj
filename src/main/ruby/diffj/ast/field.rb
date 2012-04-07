#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'

include Java

import org.incava.pmdx.ThrowsUtil
import org.incava.pmdx.FieldUtil

module DiffJ
  class FieldComparator < ItemComparator
    VARIABLE_REMOVED = "variable removed: {0}";
    VARIABLE_ADDED = "variable added: {0}";
    VARIABLE_CHANGED = "variable changed from {0} to {1}";
    VARIABLE_TYPE_CHANGED = "variable type for {0} changed from {1} to {2}";
    INITIALIZER_REMOVED = "initializer removed";
    INITIALIZER_ADDED = "initializer added";

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
        name = FieldUtil.getName(vd).image;
        names_to_vd.put(name, vd)
      end
      names_to_vd
    end

    def compareVariableTypes name, fromFieldDecl, fromVarDecl, toFieldDecl, toVarDecl
      fromType = SimpleNodeUtil.findChild(fromFieldDecl, "net.sourceforge.pmd.ast.ASTType");
      toType = SimpleNodeUtil.findChild(toFieldDecl, "net.sourceforge.pmd.ast.ASTType");

      fromTypeStr = SimpleNodeUtil.toString(fromType);
      toTypeStr = SimpleNodeUtil.toString(toType);

      if fromTypeStr != toTypeStr
        changed(fromType, toType, VARIABLE_TYPE_CHANGED, name, fromTypeStr, toTypeStr);
      end
      
      # $$$ compareVariableInits(fromVarDecl, toVarDecl)
    end

    def processAddDelVariable name, msg, fromVarDecl, toVarDecl
      fromTk = FieldUtil.getName(fromVarDecl);
      toTk = FieldUtil.getName(toVarDecl);
      changed(fromTk, toTk, msg, name);
    end
    
    def compare_variables from, to
      fromVarDecls = SimpleNodeUtil.snatchChildren(from, "net.sourceforge.pmd.ast.ASTVariableDeclarator");
      toVarDecls = SimpleNodeUtil.snatchChildren(to, "net.sourceforge.pmd.ast.ASTVariableDeclarator");

      fromNamesToVD = make_vd_map(fromVarDecls);
      toNamesToVD = make_vd_map(toVarDecls);

      names = java.util.TreeSet.new();
      names.addAll(fromNamesToVD.keySet());
      names.addAll(toNamesToVD.keySet());

      names.each do |name|
        fromVarDecl = fromNamesToVD.get(name);
        toVarDecl = toNamesToVD.get(name);

        if fromVarDecl && toVarDecl
          compareVariableTypes(name, from, fromVarDecl, to, toVarDecl)
        elsif fromVarDecls.size() == 1 && toVarDecls.size() == 1
          processChangedVariable(fromVarDecls.get(0), toVarDecls.get(0));
        elsif fromVarDecl.nil?
          processAddDelVariable(name, VARIABLE_ADDED, fromVarDecls.get(0), toVarDecl);
        else
          processAddDelVariable(name, VARIABLE_REMOVED, fromVarDecl, toVarDecls.get(0));
        end
      end
    end

    def compare_xxx from, to
      compare_modifiers from, to
      compare_variables from, to
    end
  end
end
