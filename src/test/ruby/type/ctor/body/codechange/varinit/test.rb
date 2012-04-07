#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/ctor/body/codechange/tc'

module DiffJ::Type::Ctor::Body::CodeChange
  class VarInitTestCase < TestCase
    def test_unchanged
      run_test 'Unchanged'
    end

    def test_added
      run_test 'Added', added_add("Added()", loc(1, 26), loc(1, 26), loc(10, 1), loc(11, 2))
    end

    def test_changed
      run_test 'Changed', changed("Changed()", loc(29, 77), loc(29, 77), loc(6, 1), loc(6, 2))
    end

    def test_unchanged
      run_test 'Unchanged'
    end

    def test_removed
      # $$$ this is screwy, that in a method, this is a code removed, but here's it's a code change.
      # another thing to fix.
      chg = make_fdiff_change format(DiffJ::ItemComparator::CODE_CHANGED, "Removed()"), loc(6, 2), loc(6, 12), loc(9, 43), loc(9, 43)
      run_test 'Removed', chg
    end
  end
end
