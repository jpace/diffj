#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/type'

include Java

import org.incava.diffj.DiffComparator
import org.incava.diffj.TypeDiff
import org.incava.pmdx.CompilationUnitUtil
import org.incava.pmdx.TypeDeclarationUtil

module DiffJ
  class TpsDiff < DiffComparator
    include Loggable

    TYPE_CHANGED_FROM_CLASS_TO_INTERFACE = "type changed from class to interface"
    TYPE_CHANGED_FROM_INTERFACE_TO_CLASS = "type changed from interface to class"

    TYPE_DECLARATION_ADDED = "type declaration added: {0}"
    TYPE_DECLARATION_REMOVED = "type declaration removed: {0}"
    
    def initialize diffs
      super diffs
    end

    def make_td_map types
      names_to_tds = Hash.new
      types.to_a.each do |type|
        tk = TypeDeclarationUtil.getName(type)
        if tk
          names_to_tds[tk.image] = type
        end
      end
      return names_to_tds
    end

    def compare cua, cub
      info "cua: #{cua}"
      info "cub: #{cub}"

      a_types = CompilationUnitUtil.getTypeDeclarations cua
      b_types = CompilationUnitUtil.getTypeDeclarations cub
      
      a_names_to_tds = make_td_map a_types
      b_names_to_tds = make_td_map b_types
      
      names = a_names_to_tds.keys + b_names_to_tds.keys

      info "names: #{names}"

      names.each do |name|
        atd = a_names_to_tds[name]
        btd = b_names_to_tds[name]

        if atd.nil?
          b_name = TypeDeclarationUtil.getName btd
          added cua, btd, TYPE_DECLARATION_ADDED, b_name.image
        elsif btd.nil?
          a_name = TypeDeclarationUtil.getName atd
          deleted atd, cub, TYPE_DECLARATION_REMOVED, a_name.image
        else
          differ = TypeComparator.new file_diffs
          differ.compare_xxx atd, btd
        end
      end
    end
  end
end
