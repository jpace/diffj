#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/method/parameters/tc'

module DiffJ::Type::Method::Parameters
  class ReorderTestCase < TestCase
    def changed_msg_fmt
      DiffJ::FunctionComparator::PARAMETER_REORDERED
    end  

    def test_changed
      run_test('Changed', changed("idx", 0, 1, loc(2, 22), loc(2, 24), loc(2, 35), loc(2, 37)),
               changed("down", 1, 0, loc(2, 34), loc(2, 37), loc(2, 25), loc(2, 28)))
    end
  end
end
