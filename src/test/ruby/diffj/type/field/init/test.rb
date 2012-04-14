#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/field/tc'
require 'diffj/ast/field'

module DiffJ::Type::Field
  class InitTestCase < TestCase
    def added_msg_fmt
      DiffJ::FieldComparator::INITIALIZER_ADDED
    end  

    def changed_msg_fmt
      DiffJ::FieldComparator::CODE_CHANGED
    end  

    def removed_msg_fmt
      DiffJ::FieldComparator::INITIALIZER_REMOVED
    end

    def test_unchanged
      run_test 'Unchanged'
    end

    def test_added
      run_test 'Added', added_change("added", loc(1, 17), loc(1, 17), loc(7, 1), loc(7, 2))
    end

    def test_changed
      run_test 'Changed', changed("i", loc(2, 13), loc(2, 17), loc(2, 13), loc(2, 13))
    end

    def test_unchanged
      run_test 'Unchanged'
    end

    def test_removed
      run_test 'Removed', removed_change('removed', loc(2, 18), loc(2, 22), loc(2, 12), loc(2, 14))
    end
  end
end
