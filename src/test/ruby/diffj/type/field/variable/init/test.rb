#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/field/variable/tc'
require 'diffj/ast/field'

module DiffJ::Type::Field::Variable
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
      run_test 'Added', added_change("", loc(2, 9), loc(2, 13), loc(2, 17), loc(2, 19))
    end

    def test_changed
      run_test 'Changed', changed("changed", loc(2, 22), loc(2, 25), loc(2, 22), loc(2, 26))
    end

    def test_removed
      run_test 'Removed', removed_change("", loc(2, 15), loc(2, 17), loc(2, 10), loc(2, 11))
    end
  end
end
