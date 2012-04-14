#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/tc'
require 'diffj/ast/item'

include Java

module DiffJ::Type
  class ModifiersTestCase < TestCase
    def added_msg_fmt
      DiffJ::ItemComparator::MODIFIER_ADDED
    end  

    def removed_msg_fmt
      DiffJ::ItemComparator::MODIFIER_REMOVED
    end

    def test_added
      run_test 'Added', added_change('abstract', loc(1, 1), loc(1, 6), loc(1, 8))
    end

    def test_removed
      run_test 'Removed', removed_change('strictfp', loc(1, 8), loc(1, 1), loc(1, 6))
    end
  end
end
