#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/ctor/tc'
require 'diffj/ast/function'

include Java

module DiffJ::Type::Ctor::Parameters
  class TestCase < DiffJ::Type::Ctor::TestCase
    def added_msg_fmt
      DiffJ::FunctionComparator::PARAMETER_ADDED
    end  

    def removed_msg_fmt
      DiffJ::FunctionComparator::PARAMETER_REMOVED
    end
  end
end
