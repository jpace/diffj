#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/ctor/tc'

module DiffJ::Type::Declarations::Ctor
  class WithParamsTestCase < TestCase
    def test_added
      run_test 'Added', added_add("Added(String, Double)", loc(1, 1), loc(4, 1), loc(2, 12), loc(3, 5))
    end

    def test_removed
      run_test 'Removed', removed_delete("Removed(java.util.List<String>)", loc(2, 12), loc(3, 5), loc(1, 1), loc(4, 1))
    end
  end
end
