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

      class CtxHighltWriter < DiffContextHighlightWriter
        include Loggable

        # The color for added code.
        COLOR_ADDED = org.incava.ijdk.util.ANSI::YELLOW

        # The color for deleted code.
        COLOR_DELETED = org.incava.ijdk.util.ANSI::RED

        def initialize fromContents, toContents
          super
          
          @fromContents = fromContents
          @toContents = toContents

          info "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$".yellow
        end
      end
    end
  end
end
