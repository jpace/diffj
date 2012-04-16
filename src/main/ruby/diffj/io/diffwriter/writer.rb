#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

java_import org.incava.analysis.DiffNoContextWriter
java_import org.incava.analysis.DiffContextHighlightWriter

module DiffJ
  module IO
    module Diff
      EOLN = "\n"               # $$$ @todo make OS-independent

      class DiffWrtr
      end
    end
  end
end

