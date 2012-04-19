#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  # a string (like MessageFormat) that can substitute parameters
  class ResourceString < String
    def format *args
      str = dup
      args.each_with_index do |arg, idx|
        str.sub! '{' + idx.to_s + '}', arg.to_s
      end
      str
    end
  end
end
