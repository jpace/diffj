#!/usr/bin/jruby -w
# -*- ruby -*-

require 'imports/tc'

include Java

class DiffJ::Imports::StatementTestCase < DiffJ::Imports::TestCase
  def added_msg_fmt
    ::DiffJ::ImportsComparator::IMPORT_ADDED
  end  

  def removed_msg_fmt
    ::DiffJ::ImportsComparator::IMPORT_REMOVED
  end

  def test_added
    run_imp_test 'Added', added_add('org.incava.Added', loc(1, 1), loc(1, 27), loc(2, 1), loc(2, 24))
  end

  def test_removed
    run_imp_test 'Removed', removed_delete('org.incava.Removed', loc(2, 1), loc(2, 26), loc(1, 1), loc(1, 27))
  end
end
