#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/body/codechange/tc'

module DiffJ::Type::Method::Body::CodeChange
  class VarInitTestCase < TestCase
    def test_unchanged
      run_test 'Unchanged'
    end

    def test_added
      run_test 'Added', added_add("added()", loc(1, 31), loc(1, 31), loc(11, 1), loc(12, 2))
    end

    def test_changed
      run_test 'Changed', changed("changed()", loc(2, 88), loc(2, 88), loc(3, 31), loc(3, 33))
    end

    def test_removed
      run_test 'Removed', removed_delete("removed()", loc(3, 10), loc(3, 16), loc(11, 11), loc(11, 11))
    end
  end
end
