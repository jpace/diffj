#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/tc'
require 'diffj/ast/method'

module DiffJ::Type::Method
  class ReturnTypeTestCase < TestCase
    def changed_msg_fmt
      DiffJ::MethodComparator::RETURN_TYPE_CHANGED
    end  

    def test_changed
      run_test 'Changed', changed("int", "double", loc(2, 5), loc(2, 5))
    end
  end
end
