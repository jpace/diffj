#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/method/tc'

import org.incava.diffj.MethodDiff
import org.incava.diffj.ItemDiff

module DiffJ::Type::Method
  class AccessTestCase < TestCase
    def added_msg_fmt
      ItemDiff::ACCESS_ADDED
    end  

    def changed_msg_fmt
      ItemDiff::ACCESS_CHANGED
    end  

    def removed_msg_fmt
      ItemDiff::ACCESS_REMOVED
    end

    def test_added
      run_test 'Added', added_change("protected", loc(2, 5), loc(2, 8), loc(2, 5), loc(2, 13))
    end

    def test_changed
      run_test 'Changed', changed("protected", "private", loc(2, 5), loc(2, 5))
    end

    def test_removed
      run_test 'Removed', removed_change("public", loc(2, 5), loc(2, 5), loc(2, 8))
    end
  end
end
