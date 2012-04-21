#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/diffjtestcase'
require 'diffj/util/diff/traverser'
require 'diffj/util/diff/traverser_orig'

include Java
include DiffJ::DiffLCS

class DiffJ::DiffTraverserTestCase < DiffJ::TestCase
  include Loggable

  LCSDelta = DiffJ::DiffLCS::Delta

  delta = LCSDelta.new 4, 4, 5, 6
  Log.info "delta: #{delta.class}".on_blue

  # DiffDelta = org.incava.ijdk.util.diff.Difference

  def run_diff_test from, to, exp = nil
    info "from: #{from}"
    info "to: #{to}"

    diff = org.incava.ijdk.util.diff::Diff.new from, to
    javaresult = diff.diff()

    (0 ... javaresult.size).each do |idx|
      # info "javaresult [#{idx}]: #{javaresult[idx].to_s}".yellow
    end

    # expected << org.incava.ijdk.util.diff::Difference.new(0, 1, 0, -1)
    matches = LCS.new(from, to).matches

    trav = Traverser.new matches, from.size, to.size

    jrubyresult = trav.diffs

    # info "jrubyresult: #{jrubyresult.inspect}".yellow
    assert_equal javaresult.size, jrubyresult.size

    (0 ... javaresult.size).each do |idx|
      # info "javaresult [#{idx}]: #{javaresult[idx].inspect}".yellow
      # info "jrubyresult[#{idx}]: #{jrubyresult[idx].inspect}".cyan
      assert_equal jrubyresult[idx], javaresult[idx], "idx: #{idx}"
    end

    if exp
      # info "exp: #{exp.inspect}".yellow
      assert_equal exp.size, jrubyresult.size
      
      (0 ... exp.size).each do |idx|
        # info "exp        [#{idx}]: #{exp[idx].inspect}".yellow
        # info "jrubyresult[#{idx}]: #{jrubyresult[idx].inspect}".cyan
        assert_equal exp[idx], jrubyresult[idx], "idx: #{idx}"
      end
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
end
