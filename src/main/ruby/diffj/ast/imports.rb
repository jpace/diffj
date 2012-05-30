#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/ast/element'

module DiffJ
  class ImportsComparator < ElementComparator

    IMPORT_REMOVED = "import removed: {0}"
    IMPORT_ADDED = "import added: {0}"
    IMPORT_SECTION_REMOVED = "import section removed"
    IMPORT_SECTION_ADDED = "import section added"

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

    def mark_import_section_added from, toimports
      fromtk = first_type_token from
      added fromtk, fromtk, toimports[0].token(0), toimports[-1].token(-1), IMPORT_SECTION_ADDED
    end
    
    def mark_import_section_removed fromimports, to
      totk = first_type_token to
      deleted fromimports[0].token(0), fromimports[-1].token(-1), totk, totk, IMPORT_SECTION_REMOVED
    end

    def make_import_map imports
      name_to_imp = Hash.new
      imports.each do |imp|
        str = import_to_string imp
        name_to_imp[str] = imp
      end
      name_to_imp
    end

    def compare_import_blocks fromimports, toimports
      from_names_to_imps = make_import_map fromimports
      to_names_to_imps = make_import_map toimports

      names = from_names_to_imps.keys + to_names_to_imps.keys

      names.each do |name|
        fromimp = from_names_to_imps[name]
        toimp = to_names_to_imps[name]
            
        if fromimp.nil?
          added fromimports[0], toimp, IMPORT_ADDED, name
        elsif toimp.nil?
          deleted fromimp, toimports[0], IMPORT_REMOVED, name
        end
      end
    end

    def compare from, to
      fromimports = from.imports
      toimports = to.imports
      
      if fromimports.empty?
        if !toimports.empty?
          mark_import_section_added from, toimports
        end
      elsif toimports.empty?
        mark_import_section_removed fromimports, to
      else
        compare_import_blocks fromimports, toimports
      end
    end
  end
end
