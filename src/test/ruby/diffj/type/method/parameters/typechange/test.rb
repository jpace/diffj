#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/parameters/tc'

module DiffJ::Type::Method::Parameters
  class TypeChangeTestCase < TestCase
    def changed_msg_fmt
      DiffJ::FunctionComparator::PARAMETER_TYPE_CHANGED
    end  

    def test_changed
      # I think these positions are off: the first, for example, should probably be 2.18:2.20
      run_test 'Changed', changed("int", "java.util.List<Integer>", loc(2, 18), loc(2, 22), loc(2, 18), loc(2, 42))
    end
  end
end
