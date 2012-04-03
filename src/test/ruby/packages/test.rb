#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffjtestcase'
require 'diffj/ast/package'

include Java

class DiffJ::PackagesTestCase < DiffJ::TestCase
  def subdir
    'packages'
  end

  def run_pkg_test basename, *expected_fdiffs
    run_fdiff_test expected_fdiffs, subdir, basename
  end

  def added_msg_fmt
    ::DiffJ::PackageComparator::PACKAGE_ADDED
  end

  def changed_msg_fmt
    ::DiffJ::PackageComparator::PACKAGE_RENAMED
  end

  def removed_msg_fmt
    ::DiffJ::PackageComparator::PACKAGE_REMOVED
  end

  def test_package_added
    run_pkg_test 'Added', added_add('org.incava.added', loc(1, 1), loc(2, 1), loc(1, 9))
  end

  def test_package_changed
    run_pkg_test 'Changed', changed('org.incava.fromname', 'org.incava.toname', loc(1, 9), loc(1, 9))
  end

  def test_package_removed
    run_pkg_test 'Removed', removed_delete('org.incava.removed', loc(1, 9), loc(1, 1), loc(2, 1))
  end

  def test_package_unchanged
    run_pkg_test 'NoChange'
  end

  def test_package_none
    run_pkg_test 'None'
  end
end
