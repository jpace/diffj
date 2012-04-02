#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/ctor/tc'

module DiffJ::Type::Declarations::Ctor
  class NoParamsTestCase < TestCase
    def test_added
      run_test 'Added', added_add("Added()", loc(1, 1), loc(7, 1), loc(5, 5), loc(6, 5))
    end

    def test_removed
      run_test 'Removed', removed_delete("Removed()", loc(5, 12), loc(6, 5), loc(1, 1), loc(5, 1))
    end

    def test_unchanged
      run_test 'Unchanged'
    end
  end
end
