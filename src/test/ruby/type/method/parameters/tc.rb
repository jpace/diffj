#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/method/tc'
require 'diffj/ast/function'

include Java

module DiffJ::Type::Method::Parameters
  class TestCase < DiffJ::Type::Method::TestCase
    def added_msg_fmt
      DiffJ::FunctionComparator::PARAMETER_ADDED
    end  

    def removed_msg_fmt
      DiffJ::FunctionComparator::PARAMETER_REMOVED
    end
  end
end
