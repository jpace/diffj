#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/method/parameters/tc'

module DiffJ::Type::Method::Parameters
  class ReorderRenameTestCase < TestCase
    def changed_msg_fmt
      DiffJ::FunctionComparator::PARAMETER_REORDERED_AND_RENAMED
    end  

    def test_changed
      run_test('Changed', changed("idx", 0, 1, "index", loc(2, 22), loc(2, 24), loc(2, 34), loc(2, 38)),
               changed("d", 1, 0, "dbl", loc(2, 34), loc(2, 34), loc(2, 25), loc(2, 27)))
    end
  end
end
