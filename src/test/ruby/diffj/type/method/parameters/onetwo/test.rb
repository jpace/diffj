#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/parameters/tc'

module DiffJ::Type::Method::Parameters
  class OneTwoTestCase < TestCase
    def test_added
      run_test 'Added', added_change("str", loc(2, 15), loc(2, 21), loc(2, 23), loc(2, 32))
    end

    def test_removed
      run_test 'Removed', removed_change("file", loc(2, 47), loc(2, 63), loc(2, 17), loc(2, 45))
    end
  end
end
