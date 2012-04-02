#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/innertype/cls/tc'

module DiffJ::Type::Declarations::InnerType::Cls
  class OuterClsTestCase < TestCase
    def test_added
      run_test 'Added', added_add("InnerAdded", loc(1, 1), loc(6, 1), loc(3, 5), loc(6, 5))
    end

    def test_removed
      run_test 'Removed', removed_delete("InnerRemoved", loc(14, 5), loc(16, 5), loc(1, 1), loc(56, 1))
    end

    def test_unchanged
      run_test 'Unchanged'
    end
  end
end
