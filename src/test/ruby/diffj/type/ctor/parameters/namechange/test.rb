#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/ctor/parameters/tc'

module DiffJ::Type::Ctor::Parameters
  class NameChangeTestCase < TestCase
    def changed_msg_fmt
      DiffJ::FunctionComparator::PARAMETER_NAME_CHANGED
    end  

    def test_changed
      run_test 'Changed', changed("size", "sz", loc(2, 22), loc(2, 22))
    end
  end
end
