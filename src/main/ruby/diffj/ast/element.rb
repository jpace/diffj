#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/analysis/delta'

include Java

module DiffJ
  class ElementComparator
    include Loggable

    attr_reader :filediffs

    def initialize filediffs
      @filediffs = filediffs
    end

    def add ref
      @filediffs << ref
    end

    def changed *args
      chgobj = Change.new args
      @filediffs << chgobj.filediff
    end

    def added *args
      addobj = Add.new args
      @filediffs << addobj.filediff
    end

    def deleted *args
      remobj = Remove.new args
      @filediffs << remobj.filediff
    end
  end    
end
