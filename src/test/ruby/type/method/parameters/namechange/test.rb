#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/method/parameters/tc'

module DiffJ::Type::Method::Parameters
  class NameChangeTestCase < TestCase
    def changed_msg_fmt
      DiffJ::FunctionComparator::PARAMETER_NAME_CHANGED
    end  

    def test_changed
      run_test 'Changed', changed("idx", "index", loc(2, 22), loc(2, 22))
    end
  end
end
