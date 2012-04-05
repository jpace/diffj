#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/method/body/tc'
require 'diffj/ast/item'

module DiffJ::Type::Method::Body
  class ZeroOneTestCase < TestCase
    def test_added
      mod = make_fdiff_change format(DiffJ::ItemComparator::MODIFIER_REMOVED, "abstract"), loc(2, 5), loc(2, 12), loc(2, 5), loc(2, 8)
      block = make_fdiff_change DiffJ::MethodComparator::METHOD_BLOCK_ADDED, loc(2, 14), loc(2, 26), loc(2, 5), loc(2, 19)

      run_test 'Added', mod, block
    end
    
    def test_removed
      mod = make_fdiff_change format(DiffJ::ItemComparator::MODIFIER_ADDED, "abstract"), loc(2, 5), loc(2, 8), loc(2, 5), loc(2, 12)
      block = make_fdiff_change DiffJ::MethodComparator::METHOD_BLOCK_REMOVED, loc(2, 5), loc(3, 5), loc(2, 14), loc(2, 28)

      run_test 'Removed', mod, block
    end
  end
end
