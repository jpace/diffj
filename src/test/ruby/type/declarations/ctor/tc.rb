#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/tc'

include Java

import org.incava.diffj.TypeDiff

module DiffJ::Type::Declarations::Ctor
  class TestCase < DiffJ::Type::Declarations::TestCase
    def added_msg_fmt
      TypeDiff::CONSTRUCTOR_ADDED
    end  

    def changed_msg_fmt
      raise "not implemented"
    end

    def removed_msg_fmt
      TypeDiff::CONSTRUCTOR_REMOVED
    end
  end
end
