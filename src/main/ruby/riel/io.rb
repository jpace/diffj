#!/usr/bin/ruby -w
# -*- ruby -*-

require 'riel/file'

class IO
  $-w = false

  # Reads the stream into an array. It works even when $/ == nil, which
  # works around a problem in Ruby 1.8.1.
  def readlines
    contents = []
    while ((line = gets) && line.length > 0)
      contents << line
    end
    contents
  end

  $-w = true

  class << self
    def writelines name, lines
      fpn = File._to_pathname name
      
      fpn.open(File::WRONLY | File::TRUNC | File::CREAT) do |f|
        f.puts lines
      end

      fpn
    end
    
    def numlines io
      io.readlines.size
    end
  end
end
