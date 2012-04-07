#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

import org.incava.pmdx.SimpleNodeUtil

class Java::net.sourceforge.pmd.ast::SimpleNode
  def get_children_serially
    puts "self: #{self}".on_red
    SimpleNodeUtil.getChildrenSerially self
  end

  def parent
    SimpleNodeUtil.getParent self
  end

  def to_string
    SimpleNodeUtil.toString self
  end
end
