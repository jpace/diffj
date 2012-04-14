#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/declarations/innertype/cls/tc'

module DiffJ::Type::Declarations::InnerType::Cls
  class OuterIfaceTestCase < TestCase
    def test_added
      run_test 'Added', added_add("InnerAdded", loc(1, 1), loc(9, 1), loc(13, 5), loc(15, 5))
    end

    def test_removed
      run_test 'Removed', removed_delete("InnerRemoved", loc(11, 5), loc(14, 5), loc(3, 8), loc(10, 1))
    end

    def test_unchanged
      run_test 'Unchanged'
    end
  end
end
