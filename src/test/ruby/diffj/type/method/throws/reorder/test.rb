#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/throws/tc'

module DiffJ::Type::Method::Throws
  class ReorderTestCase < TestCase
    def changed_msg_fmt
      DiffJ::FunctionComparator::THROWS_REORDERED
    end  

    def test_changed
      run_test('Changed', changed("java.security.PrivilegedActionException", 0, 1, loc(2, 27), loc(2, 65), loc(2, 55), loc(2, 93)),
               changed("java.awt.HeadlessException", 1, 0, loc(2, 68), loc(2, 93), loc(2, 27), loc(2, 52)))
    end
  end
end
