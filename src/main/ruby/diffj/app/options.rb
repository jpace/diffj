#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'java'
require 'riel'

include Java

module DiffJ
  class Options < org.incava.diffj.Options
    include Loggable
    
  end
end
