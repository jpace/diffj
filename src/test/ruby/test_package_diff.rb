#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffjtestcase'

include Java

import org.incava.diffj.PackageDiff

class DiffJPackageTestCase < DiffJTestCase
  def added_msg name
    format PackageDiff::PACKAGE_ADDED, name
  end

  def changed_msg fromname, toname
    format PackageDiff::PACKAGE_RENAMED, fromname, toname
  end

  def removed_msg name
    format PackageDiff::PACKAGE_REMOVED, name
  end

  def pkg_added name, from_start, from_end, to_start
    make_fdiff_add(added_msg(name), from_start, from_end, to_start, loctext(to_start, name))
  end

  def pkg_changed from_name, to_name, from_start, to_start
    make_fdiff_change(changed_msg(from_name, to_name), from_start, loctext(from_start, from_name), to_start, loctext(to_start, to_name))
  end

  def pkg_removed name, from_start, to_start, to_end
    make_fdiff_delete(removed_msg(name), from_start, loctext(from_start, name), to_start, to_end)
  end

  def run_pkg_test expected_fdiffs, basename
    run_fdiff_test expected_fdiffs, 'pkgdiffs', basename
  end

  def test_package_added
    name = "org.incava.added"
    expected_fdiffs = Array.new
    expected_fdiffs << pkg_added(name, loc(1, 1), loc(2, 1), loc(1, 9))
    run_pkg_test expected_fdiffs, 'PkgAdded'
  end

  def test_package_changed
    fromname, toname = "org.incava.fromname", "org.incava.toname"
    expected_fdiffs = Array.new
    expected_fdiffs << pkg_changed(fromname, toname, loc(1, 9), loc(1, 9))
    run_pkg_test expected_fdiffs, 'PkgChanged'
  end

  def test_package_removed
    name = "org.incava.removed"
    expected_fdiffs = Array.new
    expected_fdiffs << pkg_removed(name, loc(1, 9), loc(1, 1), loc(2, 1))
    run_pkg_test expected_fdiffs, 'PkgRemoved'
  end

  def test_package_unchanged
    expected_fdiffs = Array.new
    run_pkg_test expected_fdiffs, 'PkgNoChange'
  end

  def test_package_none
    expected_fdiffs = Array.new
    run_pkg_test expected_fdiffs, 'PkgNone'
  end
end
