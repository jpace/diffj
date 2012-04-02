#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/tc'

include Java

import org.incava.diffj.TypeDiff

module DiffJ::Type::Declarations::Method
  class TestCase < DiffJ::Type::Declarations::TestCase
    def added_msg_fmt
      TypeDiff::METHOD_ADDED
    end  

    def changed_msg_fmt
      TypeDiff::METHOD_CHANGED    # not implemented
    end

    def removed_msg_fmt
      TypeDiff::METHOD_REMOVED
    end
  end
end
