#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/method/parameters/reorder/tc'

module DiffJ::Type::Method::Parameters::Reorder
  class TypeChangeTestCase < TestCase
    def changed_msg_fmt
      DiffJ::FunctionComparator::PARAMETER_REORDERED_AND_TYPE_CHANGED
    end

    def test_changed
      run_test('Changed', changed("idx", 0, 1, "Integer", "int", loc(2, 18), loc(2, 28), loc(2, 31), loc(2, 37)),
               changed("down", 1, 0, "double", "Double", loc(2, 31), loc(2, 41), loc(2, 18), loc(2, 28)))
    end
  end
end
