#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/tc'
require 'diffj/ast/item'

module DiffJ::Type::Method
  class ModifierTestCase < TestCase
    def added_msg_fmt
      DiffJ::ItemComparator::MODIFIER_ADDED
    end  

    def removed_msg_fmt
      DiffJ::ItemComparator::MODIFIER_REMOVED
    end

    def test_added
      run_test 'Added', added_change("static", loc(2, 5), loc(2, 8), loc(2, 5))
    end

    def test_removed
      run_test 'Removed', removed_change("final", loc(2, 5), loc(2, 5), loc(2, 8))
    end
  end
end
