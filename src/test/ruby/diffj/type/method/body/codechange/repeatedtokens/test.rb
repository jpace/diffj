#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/body/codechange/tc'

module DiffJ::Type::Method::Body::CodeChange
  class RepeatedTokensTestCase < TestCase
    def test_changed
      run_test('Changed', 
               changed("methodName(String[])",   loc(4,   9), loc(4,  18), loc(4,   9), loc(4,  24)),
               changed("methodName(String[])",   loc(6,  17), loc(7,  19), loc(7,  17), loc(8,  18)),
               changed("methodName(String[])",   loc(7,  27), loc(7,  32), loc(8,  26), loc(8,  38)),
               added_add("methodName(String[])", loc(8,  13), loc(8,  16), loc(9,  13), loc(9,  13)),
               changed("methodName(String[])",   loc(9,  17), loc(9,  53), loc(10, 18), loc(11, 62)),
               changed("methodName(String[])",   loc(12,  9), loc(12, 22), loc(13,  9), loc(14, 32)))
    end
  end
end
