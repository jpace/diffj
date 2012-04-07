#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/field/variable/tc'
require 'diffj/ast/field'

module DiffJ::Type::Field::Variable
  class OneTwoTestCase < TestCase
    def added_msg_fmt
      DiffJ::FieldComparator::VARIABLE_ADDED
    end  

    def changed_msg_fmt
      DiffJ::FieldComparator::VARIABLE_CHANGED
    end  

    def removed_msg_fmt
      DiffJ::FieldComparator::VARIABLE_REMOVED
    end

    def test_added
      run_test 'Added', added_change("length", loc(2, 9), loc(2, 13), loc(2, 16), loc(2, 21))
    end

    def test_changed
      run_test 'Changed', changed("index", "idx", loc(2, 9), loc(2, 13), loc(2, 9), loc(2, 11))
    end

    def test_removed
      run_test 'Removed', removed_change("lastName", loc(2, 23), loc(2, 30), loc(2, 12), loc(2, 20))
    end
  end
end
