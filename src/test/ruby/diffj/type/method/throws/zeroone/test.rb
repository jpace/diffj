#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/throws/tc'

module DiffJ::Type::Method::Throws
  class ZeroOneTestCase < TestCase
    def test_added
      run_test 'Added', added_change("java.io.IOException", loc(2, 5), loc(3, 5), loc(2, 25))
    end

    def test_removed
      run_test 'Removed', removed_change("Exception", loc(2, 27), loc(2, 5), loc(3, 5))
    end
  end
end
