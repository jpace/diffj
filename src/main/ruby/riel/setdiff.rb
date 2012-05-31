#!/usr/bin/ruby -w
# -*- ruby -*-

# Compares two enumerables, treating them as sets, showing whether they are
# identical, A contains B, B contains A, or A and B contain common elements.

class SetDiff
  def SetDiff.new(a, b)
    allitems = a | b

    a_and_b    = Array.new
    a_not_in_b = Array.new
    b_not_in_a = Array.new

    allitems.each do |it|
      if a.include?(it)
        if b.include?(it)
          a_and_b
        else
          a_not_in_b
        end
      else
        b_not_in_a
      end << it
    end
    
    super(a_and_b, a_not_in_b, b_not_in_a)
  end

  attr_reader :a_and_b, :a_not_in_b, :b_not_in_a

  def initialize(a_and_b, a_not_in_b, b_not_in_a)
    @a_and_b    = a_and_b
    @a_not_in_b = a_not_in_b
    @b_not_in_a = b_not_in_a
  end

  def diff_type
    @diff_type ||= if @a_and_b.empty?
                     :no_common
                   elsif @a_not_in_b.empty?
                     if @b_not_in_a.empty?
                       :identical
                     else
                       :b_contains_a
                     end
                   elsif @b_not_in_a.empty?
                     :a_contains_b
                   else
                     :common
                   end
  end
end
