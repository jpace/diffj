#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  module IO
    class LocationRange
      attr_reader :from
      attr_reader :to

      def initialize from, to
        @from = from
        @to = to
      end

      def to_s
        "[#{from} .. #{to}]"
      end

      def == other
        (self <=> other) == 0
      end

      def <=> other
        cmp = from <=> other.from
        cmp.nonzero? || to <=> other.to
      end
    end
  end
end
