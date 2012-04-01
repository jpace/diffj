#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffjtestcase'

include Java

import org.incava.diffj.ImportsDiff

class DiffJ::ImportsStatementTestCase < DiffJ::TestCase
  def subdir
    'imports/statement'
  end

  def run_imp_test basename, *expected_fdiffs
    run_fdiff_test expected_fdiffs, subdir, basename
  end

  def added_msg_fmt
    ImportsDiff::IMPORT_ADDED
  end  

  def removed_msg_fmt
    ImportsDiff::IMPORT_REMOVED
  end

  def import_removed name, from_start, from_end, to_start, to_end
    make_fdiff_delete format(ImportsDiff::IMPORT_REMOVED, name), from_start, from_end, to_start, to_end
  end

  def test_added
    run_imp_test 'Added', added_add('org.incava.Added', loc(1, 1), loc(1, 27), loc(2, 1), loc(2, 24))
  end

  def test_removed
    run_imp_test 'Removed', import_removed('org.incava.Removed', loc(2, 1), loc(2, 26), loc(1, 1), loc(1, 27))
  end
end
