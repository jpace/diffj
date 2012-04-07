#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/method/parameters/tc'

module DiffJ::Type::Method::Parameters
  class ZeroOneTestCase < TestCase
    def test_added
      run_test 'Added', added_change("i", loc(2, 15), loc(2, 15), loc(2, 16), loc(2, 20))
    end

    def test_removed
      run_test 'Removed', removed_change("ary", loc(2, 27), loc(2, 29), loc(2, 17), loc(2, 18))
    end
  end
end
