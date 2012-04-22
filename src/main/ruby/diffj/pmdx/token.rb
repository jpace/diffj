#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

include Java

class Java::net.sourceforge.pmd.ast::Token
  include Comparable, Loggable

  def inspect
    to_s
  end

  # the spaceship+comparable trick doesn't seem to add/override
  # Java::java.lang.Object.equals, so here goes:
  def == other
    (self <=> other) == 0
  end

  def <=> other
    (kind <=> other.kind).nonzero? ||
    (image <=> other.image).nonzero? ||
      0
  end
end
