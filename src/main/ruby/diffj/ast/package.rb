#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/element'

include Java

module DiffJ
  class PackageComparator < ElementComparator
    include Loggable
    
    PACKAGE_REMOVED = "package removed: {0}"
    PACKAGE_ADDED = "package added: {0}"
    PACKAGE_RENAMED = "package renamed from {0} to {1}"

    def find_name_node parent
      parent.find_child "net.sourceforge.pmd.ast.ASTName"
    end

    def find_first_child parent
      parent.find_child
    end

    def compare_names fromnode, tonode
      fromname = find_name_node fromnode
      toname = find_name_node tonode

      if fromname.to_string != toname.to_string
        changed fromname, toname, PACKAGE_RENAMED
      end
    end

    def compare fromcu, tocu
      frompkg = fromcu.package
      topkg = tocu.package

      if frompkg
        if topkg
          compare_names frompkg, topkg
        else
          fromname = find_name_node frompkg
          topos = find_first_child(tocu) || tocu
          deleted fromname, topos, PACKAGE_REMOVED
        end
      elsif topkg
        toname = find_name_node topkg
        frompos = find_first_child(fromcu) || fromcu
        added frompos, toname, PACKAGE_ADDED
      end
    end
  end
end
