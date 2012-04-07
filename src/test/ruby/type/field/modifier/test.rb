#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/field/tc'
require 'diffj/ast/item'

module DiffJ::Type::Field
  class ModifierTestCase < TestCase
    def added_msg_fmt
      DiffJ::ItemComparator::MODIFIER_ADDED
    end  

    def removed_msg_fmt
      DiffJ::ItemComparator::MODIFIER_REMOVED
    end

    def test_added
      run_test('Added',
               added_change("static", loc(2, 5), loc(2, 7), loc(2, 5), loc(2, 10)),
               added_change("final", loc(2, 5), loc(2, 7), loc(2, 12), loc(2, 16)))
    end

    def test_removed
      run_test 'Removed', removed_change("final", loc(2, 5), loc(2, 9), loc(1, 16), loc(1, 21))
    end
  end
end
