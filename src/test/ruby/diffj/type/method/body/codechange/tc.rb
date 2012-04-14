#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/body/tc'

include Java

module DiffJ::Type::Method::Body::CodeChange
  class TestCase < DiffJ::Type::Method::Body::TestCase
    def added_msg_fmt
      DiffJ::MethodComparator::CODE_ADDED
    end  

    def changed_msg_fmt
      DiffJ::MethodComparator::CODE_CHANGED
    end  

    def removed_msg_fmt
      DiffJ::MethodComparator::CODE_REMOVED
    end
  end
end
