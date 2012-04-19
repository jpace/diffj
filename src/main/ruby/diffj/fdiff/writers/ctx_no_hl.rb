#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/fdiff/writers/context'

include Java

module DiffJ
  module FDiff
    module Writer
      class ContextNoHighlightWriter < ContextWriter
        include Loggable

        def get_line lines, lidx, from_line, from_column, to_line, to_column, is_delete
          line_to_s lines[lidx - 1], "!"
        end
      end
    end
  end
end
