#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/innertype/tc'

include Java

import org.incava.diffj.TypeDiff

module DiffJ::Type::Declarations::InnerType::Iface
  class TestCase < DiffJ::Type::Declarations::InnerType::TestCase
    def added_msg_fmt
      TypeDiff::INNER_INTERFACE_ADDED
    end  

    def changed_msg_fmt
      raise "not implemented"
    end

    def removed_msg_fmt
      TypeDiff::INNER_INTERFACE_REMOVED
    end
  end
end
