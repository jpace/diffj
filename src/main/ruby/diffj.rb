#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
# require 'riel'

include Java

import org.incava.ijdk.lang.StringExt
import org.incava.diffj.DiffJ
import org.incava.diffj.Options

class DiffJMain
  java_signature 'void main(String[])'
  def self.main args
    puts "< hello"
    puts "> world"
    c = StringExt.contains("hello", "l"[0])
    puts "c: #{c}"

    args.each do |arg|
      puts "arg: #{arg}"
    end

    opts = Options.get
    puts "opts: #{opts}"
    puts "opts.class: #{opts.class}"
    opts.methods.sort.each do |m|
      puts m
    end

    puts "opts.briefOutput: #{opts.showBriefOutput}"
    puts "opts.contextOutput: #{opts.showContextOutput}"

    DiffJ.main args
  end
end

if __FILE__ == $0
  DiffJMain.main Array.new
end
