#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/tc'
require 'diffj/ast/type'

include Java

module DiffJ::Type
  class ExtendsTestCase < TestCase
    def added_msg_fmt
      DiffJ::TypeComparator::EXTENDED_TYPE_ADDED
    end  

    def changed_msg_fmt
      DiffJ::TypeComparator::EXTENDED_TYPE_CHANGED
    end

    def removed_msg_fmt
      DiffJ::TypeComparator::EXTENDED_TYPE_REMOVED
    end

    def test_added
      run_test 'Added', added_change("java.io.File", loc(1, 8), loc(2, 1), loc(1, 28))
    end

    def test_removed
      run_test 'Removed', removed_change("java.lang.StringBuilder", loc(1, 30), loc(1, 8), loc(2, 1))
    end

    def test_changed
      run_test 'Changed', changed("java.text.DateFormat", "java.text.MessageFormat", loc(1, 30), loc(1, 30))
    end
  end
end
