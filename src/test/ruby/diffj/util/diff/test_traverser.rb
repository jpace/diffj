#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/diffjtestcase'
require 'diffj/util/diff/traverser'
require 'diffj/util/diff/traverser_orig'
require 'diffj/util/diff/locosu'

require 'benchmark'

include Java
include DiffJ::DiffLCS

class DiffJ::DiffTraverserTestCase < DiffJ::TestCase
  include Loggable

  LCSDelta = DiffJ::DiffLCS::Delta

  def assert_matches_legacy jrubyresult, from, to
    diff        = org.incava.ijdk.util.diff::Diff.new from, to
    javaresult  = diff.diff
    matches     = LCS.new(from, to).matches
    trav        = Traverser.new matches, from.size, to.size
    jrubyresult = trav.diffs

    # info "jrubyresult: #{jrubyresult.inspect}".yellow
    assert_equal javaresult.size, jrubyresult.size

    (0 ... javaresult.size).each do |idx|
      assert_equal jrubyresult[idx], javaresult[idx], "idx: #{idx}"
    end
  end

  def run_diff_test from, to, exp
    info "from: #{from}"
    info "to: #{to}"

    matches     = LCS.new(from, to).matches
    trav        = Traverser.new matches, from.size, to.size
    jrubyresult = trav.diffs

    assert_matches_legacy jrubyresult, from, to
    
    assert_equal exp.size, jrubyresult.size      
    (0 ... exp.size).each do |idx|
      assert_equal exp[idx], jrubyresult[idx], "idx: #{idx}"
    end
  end

  def test_jruby_diff_abcd_cd
    from = %w{ a b c d }
    to   = %w{     c d }

    exp = [ LCSDelta.new(0, 1, 0, nil) ]
    run_diff_test from, to, exp
  end

  def test_jruby_diff_abcd_ab
    from = %w{ a b c d }
    to   = %w{ a b     }

    exp = [ 
           LCSDelta.new(2, 3, 2, nil) 
          ]
    run_diff_test from, to, exp
  end

  def test_jruby_diff_abcd_abcd
    from = %w{ a b c d }
    to   = %w{ a b c d }

    exp = [ ]
    run_diff_test from, to, exp
  end

  def test_jruby_diff_ad_abcd
    from = %w{ a     d }
    to   = %w{ a b c d }

    exp = [ 
           LCSDelta.new(1, nil, 1, 2) 
          ]
    run_diff_test from, to, exp
  end

  def test_jruby_diff_abcdefgh_abefh
    from = %w{ a b c d e f g h }
    to   = %w{ a b     e f   h }
    
    exp = [
           LCSDelta.new(2, 3, 2, nil),
           LCSDelta.new(6, 6, 4, nil)
          ]
    run_diff_test from, to, exp
  end

  def test_jruby_diff_abcehjlmnp_bcdefjklmrst
    from = %w{ a b c   e H j   l m N P }
    to   = %w{   b c d e F j k l m R S T }

    exp = [
           LCSDelta.new(0,   0,  0, nil),
           LCSDelta.new(3, nil,  2,   2),
           LCSDelta.new(4,   4,  4,   4),
           LCSDelta.new(6, nil,  6,   6),
           LCSDelta.new(8,   9,  9,  11)
          ]
    run_diff_test from, to, exp
  end    

  def test_jruby_diff_aaaabbbaaaabbbaaaabbbaaaabbb_aaaabbbabbbaaaa
    from = %w{ a a a a b b b a a a a b b b a a a a b b b a a a a b b b }
    to   = %w{ a a a a b b b a       b b b a a a a                     }

    exp = [
           LCSDelta.new(8,  10,  8, nil),
           LCSDelta.new(18, 27, 15, nil)
          ]
    run_diff_test from, to, exp
  end

  def test_jruby_diff_with_blanks
    from = [            "same", "same", "same", "", "same", "del", "",  "del" ]
    to   = [ "ins", "", "same", "same", "same", "", "same"                    ]

    exp = [
           LCSDelta.new(0, nil,  0,  1),
           LCSDelta.new(5,   7,  7, nil)
          ]
    run_diff_test from, to, exp
  end

  def test_jruby_diff_abcde_axybcje
    from = %w{ a     b c D e }
    to   = %w{ a x y b c J e }

    exp = [
           LCSDelta.new(1, nil,  1,  2),
           LCSDelta.new(3,   3,  5,  5)
          ]
    run_diff_test from, to, exp
  end

  def run_performance_test travcls, from, to
    matches = LCS.new(from, to).matches
    trav = travcls.new matches, from.size, to.size
    result = trav.diffs
  end

  def run_java_performance_test from, to
    differ = org.incava.ijdk.util.diff.Diff.new from, to
    result = differ.diff
  end

  def run_orig_locosu_performance_test from, to
    differ = DiffJ::OrigLocosu.new from, to
    result = differ.diff
  end

  def run_locosu_performance_test from, to
    differ = DiffJ::Locosu.new from, to
    result = differ.diff
  end

  def run_benchmark_test lbl, num, from, to
    Benchmark.benchmark(lbl, 15) do |x|
      x.report("jruby:orig") do
        num.times { run_performance_test OrigTraverser, from, to }
      end

      x.report("jruby:new") do 
        num.times { run_performance_test Traverser, from, to }
      end

      x.report("java:orig") do
        num.times { run_java_performance_test from, to }
      end

      x.report("java:origlocosu") do
        num.times { run_orig_locosu_performance_test from, to }
      end

      x.report("java:locosu") do
        num.times { run_locosu_performance_test from, to }
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
