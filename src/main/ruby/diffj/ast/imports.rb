#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

import org.incava.diffj.DiffComparator
import org.incava.pmdx.CompilationUnitUtil

module DiffJ
  class ImpDiff < DiffComparator
    include Loggable

    IMPORT_REMOVED = "import removed: {0}"
    IMPORT_ADDED = "import added: {0}"
    IMPORT_SECTION_REMOVED = "import section removed"
    IMPORT_SECTION_ADDED = "import section added"
    
    def initialize diffs
      super diffs
    end

    def get_imports compunit
      # this returns C-style arrays
      CompilationUnitUtil.getImports compunit
    end

    def import_to_string imp
      str = ""
      tk  = imp.first_token.next
      
      while tk
        if tk == imp.last_token
          break
        else
          str << tk.image
          tk = tk.next
        end
      end
      str
    end

    def first_type_token cu
      # this too is a C-style array
      types = CompilationUnitUtil.getTypeDeclarations cu
      t = types.length > 0 ? types[0].first_token : null
      # if there are no types (ie. the file has only a package and/or import
      # statements), then just point to the first token in the compilation unit.      
      t || cu.first_token
    end

    def first_token imports
      imports[0].getFirstToken()
    end
    
    def last_token imports
      imports[imports.length - 1].last_token
    end

    def mark_import_section_added cua, bimports
      a0 = first_type_token cua
      a1 = a0
      b0 = first_token bimports
      b1 = last_token bimports
      added a0, a1, b0, b1, IMPORT_SECTION_ADDED
    end
    
    def mark_import_section_removed aimports, cub
      a0 = first_token aimports
      a1 = last_token aimports
      b0 = first_type_token cub
      b1 = b0
      deleted a0, a1, b0, b1, IMPORT_SECTION_REMOVED
    end

    def make_import_map imports
      name_to_imp = Hash.new
      imports.each do |imp|
        str = import_to_string imp
        name_to_imp[str] = imp
      end
      name_to_imp
    end

    def compare_import_blocks aimports, bimports
      a_names_to_imps = make_import_map aimports
      b_names_to_imps = make_import_map bimports
      info "a_names_to_imps: #{a_names_to_imps}".yellow
      info "b_names_to_imps: #{b_names_to_imps}".yellow

      names = a_names_to_imps.keys + b_names_to_imps.keys

      names.each do |name|
        aimp = a_names_to_imps[name]
        bimp = b_names_to_imps[name]
            
        if aimp.nil?
          added aimports[0], bimp, IMPORT_ADDED, name
        elsif bimp.nil?
          deleted aimp, bimports[0], IMPORT_REMOVED, name
        end
      end
    end

    def compare cua, cub
      info "#######################################################".yellow

      aimports = get_imports cua
      aimports.to_a.each do |aimp|
        info "aimp: #{aimp}"
      end

      bimports = get_imports cub
      bimports.to_a.each do |bimp|
        info "bimp: #{bimp}"
      end

      aimps = aimports.to_a
      bimps = bimports.to_a
      
      if aimps.empty?
        if !bimps.empty?
          mark_import_section_added cua, bimports
        end
      elsif bimps.empty?
        mark_import_section_removed aimports, cub
      else
        compare_import_blocks aimports, bimports
      end
    end
  end
end
