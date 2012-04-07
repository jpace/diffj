#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/package'
require 'diffj/ast/imports'
require 'diffj/ast/types'

include Java

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
