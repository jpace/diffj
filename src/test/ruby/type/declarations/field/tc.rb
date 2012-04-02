#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/tc'

include Java

import org.incava.diffj.TypeDiff

module DiffJ::Type::Declarations::Field
  class TestCase < DiffJ::Type::Declarations::TestCase
    def added_msg_fmt
      TypeDiff::FIELD_ADDED
    end  

    def changed_msg_fmt
      TypeDiff::FIELD_CHANGED    # not implemented ?
    end

    def removed_msg_fmt
      TypeDiff::FIELD_REMOVED
    end
  end
end
