#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/field/variable/renamed/init/tc'
require 'diffj/ast/field'

module DiffJ::Type::Field::Variable::Renamed::Init
  class CodeChangeTestCase < TestCase
    def test_unchanged
      run_test 'Unchanged'
    end

    def test_changed
      varchg = make_fdiff_change format(DiffJ::FieldComparator::VARIABLE_CHANGED, "d", "dbl"), loc(2, 12), loc(2, 12), loc(2, 14), loc(2, 16)
      codechg = make_fdiff_change format(DiffJ::FieldComparator::CODE_CHANGED, "d"), loc(2, 16), loc(2, 18), loc(2, 20), loc(2, 23)

      run_test 'Changed', varchg, codechg
    end
  end
end
