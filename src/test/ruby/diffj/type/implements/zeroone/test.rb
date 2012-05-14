#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/implements/tc'

include Java

module DiffJ::Type::Implements
  class ZeroOneTestCase < TestCase
    def test_added
      run_test 'Added', added_change("java.lang.Comparable", loc(1, 8), loc(2, 1), loc(1, 31))
    end

    def test_removed
      run_test 'Removed', removed_change("java.util.Map", loc(1, 33), loc(1, 8), loc(2, 1))
    end

    def test_changed
      run_test 'Changed', changed("java.io.DataOutput", "java.io.DataInput", loc(1, 33), loc(1, 50), loc(1, 33), loc(1, 49))
    end
  end
end
