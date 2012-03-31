#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

import org.incava.diffj.DiffComparator
import org.incava.diffj.TypeDiff

module DiffJ
  class TypeComparator < TypeDiff
    include Loggable

    def initialize diffs
      super diffs
    end

    def compare_xxx tda, tdb
      info "tda: #{tda}"
      info "tda.class: #{tda.class}"
      info "tdb: #{tdb}"
      info "tdb.class: #{tdb.class}"

      # class or interface declaration:
      cia = TypeDeclarationUtil.getType tda
      info "cia: #{cia}; #{cia.class}"
      cib = TypeDeclarationUtil.getType tdb
      info "cib: #{cib}; #{cib.class}"
      
      if cia || cib
        info "#{cia}; #{cib}".red
        compare cia, cib
      end
    end
  end
end
