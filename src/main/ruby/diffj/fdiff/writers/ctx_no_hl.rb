#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/fdiff/writers/context'

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
