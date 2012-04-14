#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/declarations/field/tc'

module DiffJ::Type::Declarations::Field
  class OneTestCase < TestCase
    def test_added
      run_test 'Added', added_add("i", loc(1, 1), loc(2, 1), loc(2, 13), loc(2, 18))
    end

    def test_removed
      run_test 'Removed', removed_delete("str", loc(2, 22), loc(2, 32), loc(1, 1), loc(2, 1))
    end

    def test_unchanged
      run_test 'Unchanged'
    end
  end
end
