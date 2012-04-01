#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/typetestcase'

include Java

import org.incava.diffj.TypeDiff

module DiffJ
  module TypeDiff
    class DeclarationsTestCase < DiffJTypeTestCase
      def subdir
        info "self.class: #{self.class}"
        self.class.to_s.sub(%r{.*::(\w+)TestCase}, '\1').downcase
      end
      
      def added_msg_fmt
        ::TypeDiff::IMPLEMENTED_TYPE_ADDED
      end  

      def changed_msg_fmt
        ::TypeDiff::IMPLEMENTED_TYPE_CHANGED
      end

      def removed_msg_fmt
        ::TypeDiff::IMPLEMENTED_TYPE_REMOVED
      end

      def test_added
        # run_test 'Added', added("void added()...", loc(1, 8), loc(2, 1), loc(1, 31))
      end

      def test_removed
        # run_test 'Removed', removed("Runnable", loc(1, 33), loc(1, 8), loc(2, 1))
      end

      def test_changed
        # run_test 'Changed', changed("java.io.DataOutput", "java.io.DataInput", loc(1, 33), loc(1, 33))
      end
    end
  end
end
