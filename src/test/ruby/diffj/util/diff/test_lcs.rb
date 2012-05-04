#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/tc'
require 'diffj/util/diff/lcs'
require 'benchmark'

include Java
include DiffJ::DiffLCS

class DiffJ::DiffLCSTestCase < DiffJ::TestCase
  include Loggable

  def assert_matches_legacy jrubyresult, from, to
    diff = org.incava.ijdk.util.diff::Diff.new from, to
    javaresult = diff.getLongestCommonSubsequences()
    info "javaresult: #{javaresult}".magenta
    info "jrubyresult: #{jrubyresult.inspect}".yellow
    assert_equal javaresult.size, jrubyresult.size

    (0 ... javaresult.size).each do |idx|
      info "#{idx}; #{javaresult[idx]} <=> #{jrubyresult[idx]}"
      assert_equal javaresult[idx], jrubyresult[idx], "idx: #{idx}"
    end
  end

  def run_lcs_test from, to, exp
    info "from: #{from}"
    info "to: #{to}"

    jrubyresult = LCS.new(from, to).matches
    assert_matches_legacy jrubyresult, from, to
    
    assert_equal exp.size, jrubyresult.size
    (0 ... exp.size).each do |idx|
      assert_equal exp[idx], jrubyresult[idx], "idx: #{idx}"
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

  def test_jruby_lcs_with_blanks
    from = [            "same", "same", "same", "", "same", "del", "",  "del" ]
    to   = [ "ins", "", "same", "same", "same", "", "same"                    ]

    exp = [ 2, 3, 4, 5, 6 ]
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

    actual = LCS.new(from, to).matches

    assert_equal expected, actual
  end

  def run_jruby_performance_test from, to
    matches = LCS.new(from, to).matches
  end

  def run_java_performance_test from, to
    diff = org.incava.ijdk.util.diff::Diff.new from, to
    result = diff.getLongestCommonSubsequences()
  end

  def run_benchmark_test lbl, num, from, to
    Benchmark.benchmark(lbl, 15) do |x|
      x.report("jruby") do
        num.times { run_jruby_performance_test from, to }
      end

      x.report("java") do
        num.times { run_java_performance_test from, to }
      end
    end
  end

  def get_test_data chars, size
    data = Array.new
    size.times do
      data << chars[rand(chars.size)]
    end
    data
  end
  
  def test_performance
    if ARGV.empty?
      info "skipping performance test (run with \"jruby ... -n test_performance -- run\""
      return
    end

    allchars = ('a' .. 'z').to_a
    fewchars = ('a' .. 'g').to_a

    data_sets = [
                 [ 5,    5000,  7500 ],
                 [ 10,   5000,  7500 ], 
                 [ 50,    250,   500 ],
                 [ 100,    50,   100 ],
                 [ 250,    15,    20 ],
                 [ 1000,    2,     1 ]
                ]

    data_sets.each do |size, allnum, fewnum|
      [ allchars, fewchars ].each_with_index do |charset, cidx|
        from = get_test_data charset, size + rand(size)
        to = get_test_data charset, size + rand(size)

        num = cidx == 0 ? allnum : fewnum
        run_benchmark_test "#{size} #{num}\n", num, from, to
        puts
      end
    end
  end
end
