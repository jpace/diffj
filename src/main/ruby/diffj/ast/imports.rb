#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/element'

include Java

module DiffJ
  class ImportsComparator < ElementComparator
    include Loggable

    IMPORT_REMOVED = "import removed: {0}"
    IMPORT_ADDED = "import added: {0}"
    IMPORT_SECTION_REMOVED = "import section removed"
    IMPORT_SECTION_ADDED = "import section added"
    
    def initialize diffs
      super diffs
    end

    def import_to_string imp
      # skip the first token (which is "import")
      str = ""
      tk  = imp.token(0).next
      
      while tk
        if tk == imp.token(-1)
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
      types = cu.type_declarations
      t = types.length > 0 ? types[0].token(0) : nil
      # if there are no types (ie. the file has only a package and/or import
      # statements), then just point to the first token in the compilation unit.      
      t || cu.token(0)
    end

    def mark_import_section_added cua, bimports
      a0 = first_type_token cua
      a1 = a0
      b0 = bimports[0].token 0
      b1 = bimports[-1].token(-1)
      added a0, a1, b0, b1, IMPORT_SECTION_ADDED
    end
    
    def mark_import_section_removed aimports, cub
      a0 = aimports[0].token 0
      a1 = aimports[-1].token(-1)
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

    def compare from, to
      aimports = from.imports
      bimports = to.imports
      
      if aimports.empty?
        if !bimports.empty?
          mark_import_section_added from, bimports
        end
      elsif bimports.empty?
        mark_import_section_removed aimports, to
      else
        compare_import_blocks aimports, bimports
      end
    end
  end
end
