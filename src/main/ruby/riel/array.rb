#!/usr/bin/ruby -w
# -*- ruby -*-

class Array 

  $-w = false

  # Wraps the array with brackets, and inserts a comma and a space between
  # elements.

  def to_s
    "[ " + collect { |e| e.to_s }.join(", ") + " ]"
  end
  $-w = true

  # $$$ this is the same as Array.sample
  def rand
    return self[Kernel.rand(length)]
  end
  
end
