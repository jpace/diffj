#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffjtestcase'

include Java

import org.incava.diffj.ItemDiff

class DiffJTypeTestCase < DiffJTestCase
  BASEDIR = 'typediffs'

  def run_test basename, *expected_fdiffs
    run_fdiff_test expected_fdiffs, BASEDIR + '/' + subdir, basename
  end

  def subdir
    raise "subdir: must be implemented by subclasses"
  end  

  def added_msg_fmt
    raise "added_msg_fmt: must be implemented by subclasses"
  end  

  def changed_msg_fmt
    raise "changed_msg_fmt: must be implemented by subclasses"
  end  

  def removed_msg_fmt
    raise "removed_msg_fmt: must be implemented by subclasses"
  end
  
  def added what, from_start, from_end, to_start, to_end = nil
    make_fdiff_change format(added_msg_fmt, what), from_start, from_end, to_start, to_end || loctext(to_start, what)
  end

  def removed what, from_start, to_start, to_end
    make_fdiff_change format(removed_msg_fmt, what), from_start, loctext(from_start, what), to_start, to_end
  end

  def changed from, to, from_start, to_start
    make_fdiff_change format(changed_msg_fmt, from, to), from_start, loctext(from_start, from), to_start, loctext(to_start, to)
  end

end
