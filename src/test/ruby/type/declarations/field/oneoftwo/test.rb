#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/field/tc'

class DiffJ::TypeDeclarationsFieldOneOfTwo < DiffJ::TypeDeclarationsFieldTestCase
  def subdir
    # info "self.class: #{self.class}"
    # self.class.to_s.sub(%r{.*::(\w+)TestCase}, '\1').downcase
    super + '/oneoftwo'
  end
  
  def test_added
    run_test 'Added', added_add("added", loc(1, 1), loc(3, 1), loc(3, 15), loc(3, 33))
  end

  def test_removed
    run_test 'Removed', removed_delete("removed", loc(3, 13), loc(3, 33), loc(1, 1), loc(4, 1))
  end
end
