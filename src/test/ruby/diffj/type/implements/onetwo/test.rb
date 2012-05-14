#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/implements/tc'

include Java

module DiffJ::Type::Implements
  class OneTwoTestCase < TestCase
    def test_added
      run_test 'Added', added_add("java.util.List", loc(1, 20), loc(1, 50), loc(1, 31))
    end

    def test_removed
      run_test 'Removed', removed_delete("Runnable", loc(1, 33), loc(1, 22), loc(1, 45))
    end

    def test_changed
      run_test('Changed', 
               added_add("java.io.DataInput", loc(1, 22), loc(1, 72), loc(1, 33)),
               removed_delete("java.io.DataOutput", loc(1, 33), loc(1, 22), loc(1, 71)))
    end
  end
end
