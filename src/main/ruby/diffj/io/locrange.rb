#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/io/location'

include Java

module DiffJ
  module IO
    class LocationRange
      include Loggable

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
        info "self: #{self}".on_magenta
        (self <=> other) == 0
      end

      def <=> other
        info "self: #{self}".on_magenta
        cmp = from <=> other.from
        return cmp unless cmp.zero?

        to <=> other.to
      end
    end
  end
end
