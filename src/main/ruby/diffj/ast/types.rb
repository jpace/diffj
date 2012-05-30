#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/ast/element'
require 'diffj/ast/type'

module DiffJ
  class TypesComparator < ElementComparator

    TYPE_CHANGED_FROM_CLASS_TO_INTERFACE = "type changed from class to interface"
    TYPE_CHANGED_FROM_INTERFACE_TO_CLASS = "type changed from interface to class"

    TYPE_DECLARATION_ADDED = "type declaration added: {0}"
    TYPE_DECLARATION_REMOVED = "type declaration removed: {0}"
    
    def make_td_map types
      names_to_tds = Hash.new
      types.to_a.each do |type|
        tk = type.nametk
        if tk
          names_to_tds[tk.image] = type
        end
      end
      names_to_tds
    end

    def compare from_cu, to_cu
      from_types = from_cu.type_declarations
      to_types = to_cu.type_declarations
      
      from_names_to_tds = make_td_map from_types
      to_names_to_tds = make_td_map to_types

      names = from_names_to_tds.keys | to_names_to_tds.keys

      names.each do |name|
        fromtd = from_names_to_tds[name]
        totd = to_names_to_tds[name]

        if fromtd.nil?
          added from_cu, totd, TYPE_DECLARATION_ADDED, totd.namestr
        elsif totd.nil?
          deleted fromtd, to_cu, TYPE_DECLARATION_REMOVED, fromtd.namestr
        else
          differ = TypeComparator.new filediffs
          differ.compare fromtd, totd
        end
      end
    end
  end
end
