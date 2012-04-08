#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

import org.incava.pmdx.SimpleNodeUtil

class Java::net.sourceforge.pmd.ast::SimpleNode
  def get_children_serially
    puts "self: #{self}".on_red
    org.incava.pmdx.SimpleNodeUtil.getChildrenSerially self
  end

  def parent
    org.incava.pmdx.SimpleNodeUtil.getParent self
  end

  def to_string
    org.incava.pmdx.SimpleNodeUtil.toString self
  end
end
