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

  def run_lcs_test from, to, exp = nil
    info "from: #{from}"
    info "to: #{to}"

    diff = org.incava.ijdk.util.diff::Diff.new from, to
    javaresult = diff.getLongestCommonSubsequences()

    # expected << org.incava.ijdk.util.diff::Difference.new(0, 1, 0, -1)
    cmp = Comparator.new from, to
    jrubyresult = cmp.lcs

    info "jrubyresult: #{jrubyresult.inspect}".yellow
    assert_equal javaresult.size, jrubyresult.size

    (0 ... javaresult.size).each do |idx|
      info "javaresult [#{idx}]: #{javaresult[idx].inspect}".yellow
      info "jrubyresult[#{idx}]: #{jrubyresult[idx].inspect}".cyan
      assert_equal javaresult[idx], jrubyresult[idx], "idx: #{idx}"
    end

    if exp
      info "exp: #{exp.inspect}".yellow
      assert_equal exp.size, jrubyresult.size
      
      (0 ... exp.size).each do |idx|
        info "exp        [#{idx}]: #{exp[idx].inspect}".yellow
        info "jrubyresult[#{idx}]: #{jrubyresult[idx].inspect}".cyan
        assert_equal exp[idx], jrubyresult[idx], "idx: #{idx}"
      end
    end
  end

  def test_jruby_lcs_abcd_cd
    from = %w{ a b c d }
    to   = %w{     c d }

    exp = [ nil, nil, 0, 1 ]
    run_lcs_test from, to, exp
  end

  def test_jruby_lcs_abcd_ab
    from = %w{ a b c d }
    to   = %w{ a b }

    exp = [ 0, 1 ]
    run_lcs_test from, to, exp
  end

  def test_jruby_lcs_abcd_abcd
    from = %w{ a b c d }
    to   = %w{ a b c d }

    exp = [ 0, 1, 2, 3 ]
    run_lcs_test from, to, exp
  end

  def test_jruby_lcs_ad_abcd
    from = %w{ a d }
    to   = %w{ a b c d }

    exp = [ 0, 3 ]
    run_lcs_test from, to, exp
  end

  def test_jruby_lcs_abcdefgh_abefh
    from = %w{ a b c d e f g h }
    to   = %w{ a b     e f   h }
    
    exp = [ 0, 1, nil, nil, 2, 3, nil, 4 ]
    run_lcs_test from, to, exp
  end

  def test_jruby_lcs_abcehjlmnp_bcdefjklmrst
    from = %w{ a b c   e   h j   l m n p }
    to   = %w{   b c d e f   j k l m     r s t }

    exp = [ nil, 0, 1, 3, nil, 5, 7, 8 ]
    run_lcs_test from, to, exp
  end    

  def test_jruby_lcs_aaaabbbaaaabbbaaaabbbaaaabbb_aaaabbbabbbaaaa
    from = %w{ a a a a b b b a a a a b b b a a a a b b b a a a a b b b }
    to   = %w{ a a a a b b b a       b b b a a a a }

    exp = [ 0, 1, 2, 3, 4, 5, 6, 7, nil, nil, nil, 8, 9, 10, 11, 12, 13, 14 ]
    run_lcs_test from, to, exp
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
