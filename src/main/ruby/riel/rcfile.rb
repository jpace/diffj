#!/usr/bin/ruby -w
# -*- ruby -*-


# Represents a resource file, where '#' is used to comment to end of lines, and
# name/value pairs are separated by '=' or ':'.

class RCFile

  attr_reader :settings

  # Reads the RC file, if it exists, and if a block is passed, calls the block
  # with each name/value pair, which are also accessible via
  # <code>settings</code>.

  def initialize(fname, &blk)
    @settings = Array.new
    
    if File.exists?(fname)
      IO::readlines(fname).each do |line|
        line.sub!(/\s*#.*/, "")
        line.chomp!
        name, value = line.split(/\s*[=:]\s*/)
        if name && value
          name.strip!
          value.strip!
          @settings << [ name, value ]
          if blk
            blk.call(name, value)
          end
        end
      end
    end
  end
end
