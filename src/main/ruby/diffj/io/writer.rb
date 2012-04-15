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
      class NoContextWriter < DiffNoContextWriter
        def noctx_print_from sb, fdiff
          printLines sb, fdiff.getFirstLocation(), "<", fromContents
        end
      end

      class CtxHighltWriter < DiffContextHighlightWriter
      end
    end
  end
end
    
