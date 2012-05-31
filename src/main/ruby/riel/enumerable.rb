#!/usr/bin/ruby -w
# -*- ruby -*-

module Enumerable

  def collect_with_index(&blk)
    ary = []
    self.each_with_index do |item, idx|
      ary << blk.call(item, idx)
    end
    ary
  end

  NOT_NIL = Object.new

  def select_with_index(arg = NOT_NIL, &blk)
    ary = []
    self.each_with_index do |item, idx|
      ary << item if _match?(arg, item, idx, &blk)
    end
    ary
  end

  def detect_with_index(arg = NOT_NIL, &blk)
    self.each_with_index do |item, idx|
      return item if _match?(arg, item, idx, &blk)
    end
    nil
  end

  alias :orig_select  :select
  alias :orig_collect :collect
  alias :orig_detect  :detect
  alias :orig_reject  :reject

  origw = $-w
  $-w = false
  def select(arg = NOT_NIL, &blk)
    ary = []
    self.each_with_index do |item, idx|
      ary << item if _match?(arg, item, idx, &blk)
    end
    ary
  end

  def detect(arg = NOT_NIL, &blk)
    self.each_with_index do |item, idx|
      return item if _match?(arg, item, idx, &blk)
    end
    nil
  end
  $-w = origw

  def _match?(arg, item, idx, &blk)
    if blk 
      args = [ item ]
      args << idx if idx && blk.arity > 1
      blk.call(*args)
    elsif arg == NOT_NIL
      !item.nil?
    else
      arg == item
    end
  end
  
end
