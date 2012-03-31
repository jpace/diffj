#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/packages'
require 'diffj/ast/imports'
require 'diffj/ast/types'

include Java

import org.incava.diffj.DiffComparator
import org.incava.diffj.TypesDiff

module DiffJ
  class CompUnitDiff < DiffComparator
    def initialize report
      super report
    end
    
    def compare cua, cub
      diffs = file_diffs

      return unless cua && cub

      pd = PkgDiff.new diffs
      pd.compare cua, cub
      
      id = ImpDiff.new diffs
      id.compare cua, cub

      td = TpsDiff.new diffs
      td.compare cua, cub
    end
  end
end
