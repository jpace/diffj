#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/field/tc'

class DiffJ::TypeDeclarationsFieldThreeofThree < DiffJ::TypeDeclarationsFieldTestCase
  def subdir
    # info "self.class: #{self.class}"
    # self.class.to_s.sub(%r{.*::(\w+)TestCase}, '\1').downcase
    super + '/threeofthree'
  end
  
  def test_added
    run_test('Added', 
             added_add("alpha", loc(1, 1), loc(2, 1), loc(3, 19), loc(3, 50)),
             added_add("bravo", loc(1, 1), loc(2, 1), loc(5, 19), loc(5, 48)),
             added_add("charlie", loc(1, 1), loc(2, 1), loc(7, 19), loc(7, 31)))
  end

  def test_removed
    run_test('Removed', 
             removed_delete("fieldOne", loc(2, 19), loc(2, 31), loc(1, 1), loc(2, 1)),
             removed_delete("fieldTwo", loc(4, 19), loc(4, 50), loc(1, 1), loc(2, 1)),
             removed_delete("fieldThree", loc(6, 19), loc(6, 49), loc(1, 1), loc(2, 1)))
  end
end
