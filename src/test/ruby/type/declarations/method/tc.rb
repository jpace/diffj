#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/typetestcase'

include Java

import org.incava.diffj.TypeDiff

class DiffJ::TypeDeclarationsMethodTestCase < DiffJTypeTestCase
  def subdir
    # info "self.class: #{self.class}"
    # self.class.to_s.sub(%r{.*::(\w+)TestCase}, '\1').downcase
    'declarations/method'
  end
  
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
