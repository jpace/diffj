#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/ast/package'
require 'diffj/ast/imports'
require 'diffj/ast/types'

module DiffJ
  class CompUnitComparator
    def initialize report
      @filediffs = report.differences
    end
    
    def compare cua, cub
      return unless cua && cub

      pd = PackageComparator.new @filediffs
      pd.compare cua, cub
      
      id = ImportsComparator.new @filediffs
      id.compare cua, cub

      td = TypesComparator.new @filediffs
      td.compare cua, cub
    end
  end
end
