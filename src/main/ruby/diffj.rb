#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'java'
require 'riel'

include Java

import org.incava.ijdk.lang.StringExt
import org.incava.diffj.DiffJ
import org.incava.diffj.Options

import org.incava.diffj.DiffJ

class DiffJRuby < DiffJ
  def initialize brief, context, highlight, recurse, fromname, fromver, toname, tover
    # super
    $stderr.puts "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!".yellow
  end

  def process_things names
    if names.size >= 2
      tofile = java.io.File.new(names[-1])
      puts "tofile: #{tofile}".on_green

      names[0 ... -1].each do |fromname|
        puts "fromname: #{fromname}".on_blue
        process java.io.File.new(fromname), tofile
      end
    else
      $stderr.puts "usage: diffj from-file to-file"
      exit_value = 1
    end
  end
end

if __FILE__ == $0
  opts = Options.get
  names = opts.process ARGV
  
  diffj = DiffJRuby.new(opts.showBriefOutput, 
                        opts.showContextOutput, 
                        opts.highlightOutput,
                        opts.recurse,
                        opts.firstFileName, opts.getFromSource,
                        opts.getSecondFileName, opts.getToSource)
  rarray = Array.new
  names.each do |name|
    rarray << name
  end

  diffj.process_things rarray
  puts "exiting with value: #{diffj.exit_value}"
  exit diffj.exit_value
end

__END__

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
