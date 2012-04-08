#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

import org.incava.pmdx.SimpleNodeUtil

class Java::net.sourceforge.pmd.ast::SimpleNode
  def get_children_serially
    puts "self: #{self}"
    org.incava.pmdx.SimpleNodeUtil.getChildrenSerially self
  end

  def parent
    org.incava.pmdx.SimpleNodeUtil.getParent self
  end

  def to_string
    org.incava.pmdx.SimpleNodeUtil.toString self
  end

  def leading_tokens
    org.incava.pmdx.SimpleNodeUtil.getLeadingTokens self
  end

  def find_child clsname = nil
    org.incava.pmdx.SimpleNodeUtil.findChild self, clsname
  end

  def snatch_children clsname
    org.incava.pmdx.SimpleNodeUtil.snatchChildren self, clsname
  end

end
