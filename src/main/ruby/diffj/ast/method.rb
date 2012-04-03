#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'

include Java

import org.incava.diffj.MethodDiff

module DiffJ
  class MethodComparator < MethodDiff
    include Loggable

    METHOD_BLOCK_ADDED = "method block added"
    METHOD_BLOCK_REMOVED = "method block removed"
    
    def initialize diffs
      super
      @itemcomp = ItemComparator.new diffs
    end

    def compare_access_xxx from, to
      info "from: #{from}".on_red
      info "to  : #{to}".on_red

      @itemcomp.compare_access from, to
    end

    def compare_xxx from, to
      info "from: #{from}".on_red
      info "to  : #{to}".on_red

      compare from, to
    end
  end
end
