#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/element'

include Java

import org.incava.pmdx.CompilationUnitUtil
import org.incava.pmdx.SimpleNodeUtil

module DiffJ
  class PackageComparator < ElementComparator
    include Loggable
    
    PACKAGE_REMOVED = "package removed: {0}"
    PACKAGE_ADDED = "package added: {0}"
    PACKAGE_RENAMED = "package renamed from {0} to {1}"

    def initialize diffs
      super diffs
      info "diffs: #{diffs}"
    end

    def find_name_node from
      SimpleNodeUtil.findChild from, "net.sourceforge.pmd.ast.ASTName"
    end

    def find_first_child from
      SimpleNodeUtil.findChild from
    end

    def node_to_string node
      SimpleNodeUtil.toString node
    end

    def find_package cu
      CompilationUnitUtil.getPackage cu
    end

    def compare_names anode, bnode
      info "anode: #{anode}".cyan
      info "bnode: #{bnode}".cyan

      aname = find_name_node anode
      astr  = node_to_string aname
      bname = find_name_node bnode
      bstr  = node_to_string bname

      if astr != bstr
        changed aname, bname, PACKAGE_RENAMED
      end
    end

    def compare cua, cub
      info "cua: #{cua}"
      info "cub: #{cub}"

      apkg = find_package cua
      bpkg = find_package cub

      info "apkg: #{apkg}"
      info "bpkg: #{bpkg}"

      if apkg
        if bpkg
          compare_names apkg, bpkg
        else
          aname = find_name_node apkg
          bpos = find_first_child(cub) || cub
          deleted aname, bpos, PACKAGE_REMOVED
        end
      elsif bpkg
        bname = find_name_node bpkg
        apos = find_first_child(cua) || cua
        added apos, bname, PACKAGE_ADDED
      end
    end
  end
end
