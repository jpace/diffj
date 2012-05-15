#!/usr/bin/jruby -w
# -*- ruby -*-

module DiffJ
  module IO
    class LocationRange
      include Comparable
      
      attr_reader :from
      attr_reader :to

      def initialize from, to
        @from = from
        @to = to
      end

      def inspect
        to_s
      end

      def to_s
        "[#{from} .. #{to}]"
      end

      def == other
        (self <=> other) == 0
      end

      def <=> other
        (from <=> other.from).nonzero? || (to <=> other.to).nonzero? || 0
      end
    end
  end
end
