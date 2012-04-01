#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/field/tc'

class DiffJ::TypeDeclarationsFieldOne < DiffJ::TypeDeclarationsFieldTestCase
  def subdir
    # info "self.class: #{self.class}"
    # self.class.to_s.sub(%r{.*::(\w+)TestCase}, '\1').downcase
    super + '/one'
  end
  
  def test_added
    run_test 'Added', added_add("i", loc(1, 1), loc(2, 1), loc(2, 13), loc(2, 18))
  end

  def test_removed
    run_test 'Removed', removed_delete("str", loc(2, 22), loc(2, 32), loc(1, 1), loc(2, 1))
  end
end
