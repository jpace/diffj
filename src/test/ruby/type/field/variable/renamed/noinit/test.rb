#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/field/variable/renamed/tc'

module DiffJ::Type::Field::Variable::Renamed
  class NoInitTestCase < TestCase
    def test_unchanged
      run_test 'Unchanged'
    end

    def test_changed
      run_test 'Changed', changed("fl", "flotilla", loc(2, 11), loc(2, 11))
    end
  end
end
