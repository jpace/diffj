#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/method/tc'

module DiffJ::Type::Declarations::Method
  class WithParamsTestCase < TestCase
    def test_added
      run_test 'Added', added_add("contender(String)", loc(1, 1), loc(4, 1), loc(5, 12), loc(6, 5))
    end

    def test_removed
      run_test 'Removed', removed_delete("contender(Double[], StringBuilder)", loc(2, 12), loc(3, 5), loc(1, 1), loc(4, 1))
    end
  end
end
