#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'

include Java

import org.incava.pmdx.ThrowsUtil

module DiffJ
  class FieldComparator < ItemComparator
  end
end
