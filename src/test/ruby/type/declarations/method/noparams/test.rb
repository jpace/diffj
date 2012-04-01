#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/method/tc'

class DiffJ::TypeDeclarationsMethodNoParamsTestCase < DiffJ::TypeDeclarationsMethodTestCase
  def subdir
    # info "self.class: #{self.class}"
    # self.class.to_s.sub(%r{.*::(\w+)TestCase}, '\1').downcase
    super + '/noparams'
  end
  
  def test_added
    run_test 'Added', added_add("added()", loc(1, 1), loc(2, 1), loc(2, 12), loc(3, 5))
  end

  def test_removed
    run_test 'Removed', removed_delete("removed()", loc(2, 12), loc(3, 5), loc(1, 1), loc(2, 1))
  end
end
