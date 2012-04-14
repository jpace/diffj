#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/tc'
require 'diffj/ast/function'

include Java

module DiffJ::Type::Method::Throws
  class TestCase < DiffJ::Type::Method::TestCase
    def added_msg_fmt
      DiffJ::FunctionComparator::THROWS_ADDED
    end  

    def removed_msg_fmt
      DiffJ::FunctionComparator::THROWS_REMOVED
    end
  end
end
