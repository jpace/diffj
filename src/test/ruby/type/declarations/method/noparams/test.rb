#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/method/tc'

module DiffJ::Type::Declarations::Method
  class NoParamsTestCase < TestCase
    def test_added
      run_test 'Added', added_add("added()", loc(1, 1), loc(2, 1), loc(2, 12), loc(3, 5))
    end

    def test_removed
      run_test 'Removed', removed_delete("removed()", loc(2, 12), loc(3, 5), loc(1, 1), loc(2, 1))
    end
  end
end
