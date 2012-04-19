#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/fdiff/delta'

include Java

module DiffJ
  class ElementComparator
    include Loggable

    attr_reader :filediffs

    def initialize filediffs
      @filediffs = filediffs
    end

    def add fd
      # << is method_alias'ed to add, but method aliasing just copies the
      # method; it doesn't create a "live" reference.
      @filediffs << fd
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
