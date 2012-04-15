#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  module Analysis
    # Reports differences in long form.
    class LongReport < org.incava.analysis.DetailedReport
      include Loggable
      
      def initialize writer, show_context, highilght
        info "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!".on_blue
        super
      end

      def flush
        info "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!".on_green
        super
      end
    end
  end
end
