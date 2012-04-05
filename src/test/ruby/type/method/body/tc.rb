#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/method/tc'
require 'diffj/ast/method'

include Java

module DiffJ::Type::Method::Body
  class TestCase < DiffJ::Type::Method::TestCase
    def added_msg_fmt
      DiffJ::MethodComparator::METHOD_BLOCK_ADDED
    end  

    def changed_msg_fmt
      DiffJ::MethodComparator::METHOD_BLOCK_ADDED
    end  

    def removed_msg_fmt
      DiffJ::MethodComparator::METHOD_BLOCK_REMOVED
    end
  end
end
