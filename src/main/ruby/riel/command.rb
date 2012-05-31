#!/usr/bin/ruby -w
# -*- ruby -*-
#!ruby -w

class Command

  # Runs the given command and arguments, returning the lines of output. If a
  # block is provided, then it will be called with each line of output. If the
  # block takes two arguments, then the line number is also passed to the block.

  def self.run(cmd, *args, &blk)
    lines = Array.new
    
    IO.popen("#{cmd} #{args.join(' ')}") do |io|
      lnum = 0
      io.each_line do |line|
        lines << line
        if blk
          args = [ line ]
          args << lnum if blk.arity > 1
          blk.call(*args)
        end
        lnum += 1
      end
    end
    
    lines
  end

end
