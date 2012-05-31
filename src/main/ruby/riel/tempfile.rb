#!/usr/bin/ruby -w
# -*- ruby -*-

require 'tempfile'


class Tempfile

  class << self

    alias_method :original_open, :open

    # this works around the behavior (fixed in 1.9) so that open returns
    # the new temporary file instead of nil.
    
    def open(*args, &blk)
      tempname = nil
      original_open(*args) do |tf|
        tempname = tf.path
        
        blk.call(tf) if blk
      end

      tempname
    end
  end

end
