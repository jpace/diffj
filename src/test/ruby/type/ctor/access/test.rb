#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/ctor/tc'
require 'diffj/ast/item'

import org.incava.diffj.ItemDiff

module DiffJ::Type::Ctor
  class AccessTestCase < TestCase
    def added_msg_fmt
      DiffJ::ItemComparator::ACCESS_ADDED
    end  

    def changed_msg_fmt
      DiffJ::ItemComparator::ACCESS_CHANGED
    end  

    def removed_msg_fmt
      DiffJ::ItemComparator::ACCESS_REMOVED
    end

    def test_added
      run_test 'Added', added_change("protected", loc(2, 5), loc(2, 9), loc(2, 5), loc(2, 13))
    end

    def test_changed
      run_test 'Changed', changed("protected", "private", loc(2, 5), loc(2, 5))
    end

    def test_removed
      run_test 'Removed', removed_change("public", loc(2, 5), loc(2, 5), loc(2, 11))
    end
  end
end
