#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/innertype/iface/tc'

module DiffJ::Type::Declarations::InnerType::Iface
  class OuterClsTestCase < TestCase
    def test_added
      run_test 'Added', added_add("InnerAdded", loc(1, 1), loc(2, 1), loc(2, 12), loc(4, 5))
    end

    def test_removed
      run_test 'Removed', removed_delete("InnerRemoved", loc(3, 5), loc(4, 5), loc(1, 8), loc(3, 1))
    end

    def test_unchanged
      run_test 'Unchanged'
    end
  end
end
