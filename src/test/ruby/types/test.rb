#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffjtestcase'

include Java

import org.incava.diffj.TypesDiff

class DiffJTypesTestCase < DiffJTestCase
  def subdir
    'types'
  end

  # def get_from_and_to_filenames dirname, basename
  #   fnames = %w{ d0 d1 }.collect { |subdir| TESTBED_DIR + '/' + dirname + '/' + subdir + '/' + basename + '.java' }

  #   fromname, toname = *fnames
  #   info "fromname: #{fromname}"
  #   info "toname: #{toname}"

  #   [ fromname, toname ]
  # end

  def run_types_test basename, *expected_fdiffs
    run_fdiff_test expected_fdiffs, subdir, basename
  end

  def added_msg_fmt
    TypesDiff::TYPE_DECLARATION_ADDED
  end

  def removed_msg_fmt
    TypesDiff::TYPE_DECLARATION_REMOVED
  end

  def removed name, from_start, from_end, to_start, to_end
    make_fdiff_delete format(removed_msg_fmt, name), from_start, from_end, to_start, to_end
  end

  def test_type_added
    run_types_test 'Added', added_add('TheTypeAdded', loc(1, 1), loc(2, 2), loc(4, 1), loc(5, 1))
  end

  def test_section_removed
    run_types_test 'Removed', removed("TheTypeRemoved", loc(4, 1), loc(5, 1), loc(1, 1), loc(2, 2))
  end
end
