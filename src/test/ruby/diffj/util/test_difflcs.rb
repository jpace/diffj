#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/diffjtestcase'
require 'diffj/util/difflcs'

include Java
include DiffJ::DiffLCS

class DiffJ::DiffLCSTestCase < DiffJ::TestCase
  include Loggable

  def xxxtest_java_lcs
    from = %w{ a b c d }
    to   = %w{     c d }

    # expected << org.incava.ijdk.util.diff::Difference.new(0, 1, 0, -1)
    diff = org.incava.ijdk.util.diff::Diff.new from, to
    actual = diff.getLongestCommonSubsequences()

    info "actual: #{actual}".red
    actual.each_with_index do |elmt, idx|
      info "result[#{idx}]: #{elmt}"
    end
  end

  def run_lcs_test from, to
    info "from: #{from}"
    info "to: #{to}"

    diff = org.incava.ijdk.util.diff::Diff.new from, to
    javaresult = diff.getLongestCommonSubsequences()

    # expected << org.incava.ijdk.util.diff::Difference.new(0, 1, 0, -1)
    cmp = Comparator.new from, to
    jrubyresult = cmp.lcs

    assert_equal javaresult.size, jrubyresult.size

    (0 ... javaresult.size).each do |idx|
      info "javaresult [#{idx}]: #{javaresult[idx].inspect}".yellow
      info "jrubyresult[#{idx}]: #{jrubyresult[idx].inspect}".cyan
      assert_equal javaresult[idx], jrubyresult[idx], "idx: #{idx}"
    end
  end

  def test_jruby_lcs_abcd_cd
    from = %w{ a b c d }
    to   = %w{     c d }

    run_lcs_test from, to
  end

  def test_jruby_lcs_abcd_ab
    from = %w{ a b c d }
    to   = %w{ a b }

    run_lcs_test from, to
  end

  def test_jruby_lcs_abcd_abcd
    from = %w{ a b c d }
    to   = %w{ a b c d }

    run_lcs_test from, to
  end

  def test_jruby_lcs_ad_abcd
    from = %w{ a d }
    to   = %w{ a b c d }

    run_lcs_test from, to
  end

  def test_jruby_lcs_abcdefgh_abefh
    from = %w{ a b c d e f g h }
    to   = %w{ a b     e f   h }
    
    run_lcs_test from, to
  end

  def xxxtest_java_deleted_two
    from = %w{ a b c d }
    to   = %w{     c d }

    expected = java.util.ArrayList.new
    expected << org.incava.ijdk.util.diff::Difference.new(0, 1, 0, -1)
    diff = org.incava.ijdk.util.diff::Diff.new from, to
    actual = diff.diff

    assert_equal expected, actual
  end

  def xxxtest_deleted_two
    from = %w{ a b c d }
    to   = %w{     c d }

    expected = Array.new
    expected << Delta.new(0, 1, 0, -1)

    actual = Comparator.new.compare from, to

    assert_equal expected, actual
  end
end
