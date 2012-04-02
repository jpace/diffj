#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/package'
require 'diffj/ast/imports'
require 'diffj/ast/types'

include Java

import org.incava.diffj.DiffComparator

module DiffJ
  class CompUnitDiff < DiffComparator
    def initialize report
      super report
    end
    
    def compare cua, cub
      diffs = file_diffs

      return unless cua && cub

      pd = PackageComparator.new diffs
      pd.compare cua, cub
      
      id = ImportsComparator.new diffs
      id.compare cua, cub

      td = TypesComparator.new diffs
      td.compare cua, cub
    end
  end
end
