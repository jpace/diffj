#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  module Analysis
    # Reports differences in long form.
    class DetReport < org.incava.analysis.DetailedReport
    end
  end
end
