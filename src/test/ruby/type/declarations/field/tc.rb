#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/typetestcase'

include Java

import org.incava.diffj.TypeDiff

class DiffJ::TypeDeclarationsFieldTestCase < DiffJTypeTestCase
  def subdir
    # info "self.class: #{self.class}"
    # self.class.to_s.sub(%r{.*::(\w+)TestCase}, '\1').downcase
    'declarations/field'
  end
  
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
