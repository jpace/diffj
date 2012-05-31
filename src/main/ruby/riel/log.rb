#!/usr/bin/ruby -w
# -*- ruby -*-
#
# = log.rb
#
# Logging Module
#
# Author:: Jeff Pace <jpace@incava.org>
# Documentation:: Author
#

require 'riel/ansicolor'

#
# == Log
#
# Very minimal logging output. If verbose is set, this displays the method and
# line number whence called. It can be a mixin to a class, which displays the
# class and method from where it called. If not in a class, it displays only the
# method.
# 
# Remember: all kids love log.
#
# == Examples
#
# See the unit tests in log_test.rb
#
# == Usage
#
# The most general usage is simply to call:
#
#  Log.log "some message"
#
#  That will simply log the given message.
#
#  class YourClass
#    include Loggable
#
#    def some_method(...)
#      log "my message"
# 
#  That will log from the given class and method, showing the line number from
#  which the logger was called.
#
#    def another_method(...)
#      stack "my message"
# 
#  That will produce a stack trace from the given location.
# 

module RIEL
  class Log
    $LOGGING_LEVEL = nil

    attr_accessor :quiet
    attr_accessor :output
    attr_accessor :colorize_line
    attr_accessor :level
    attr_accessor :ignored_files
    attr_accessor :ignored_methods
    attr_accessor :ignored_classes
    attr_accessor :trim

    # 
    
    module Severity
      DEBUG = 0
      INFO  = 1
      WARN  = 2
      ERROR = 3
      FATAL = 4
    end

    include Log::Severity

    FRAME_RE = Regexp.new('(.*):(\d+)(?::in \`(.*)\')?')

    def initialize
      set_defaults
    end
    
    def verbose=(v)
      @level = case v
               when TrueClass 
                 DEBUG
               when FalseClass 
                 FATAL
               when Integer
                 v
               end
    end

    def set_defaults
      $LOGGING_LEVEL   = @level = FATAL
      @ignored_files   = {}
      @ignored_methods = {}
      @ignored_classes = {}
      @width           = 0
      @output          = $stdout
      @colors          = []
      @colorize_line   = false
      @quiet           = false
      @trim            = true

      set_default_widths
    end

    def set_default_widths
      set_widths(-15, 4, -20)
    end

    def verbose
      level <= DEBUG
    end

    # Assigns output to a file with the given name. Returns the file; client
    # is responsible for closing it.
    def outfile= f
      @output = if f.kind_of?(IO) then f else File.new(f, "w") end
    end

    # Creates a printf format for the given widths, for aligning output. To lead
    # lines with zeros (e.g., "00317") the line_width argument must be a string,
    # not an integer.
    def set_widths file_width, line_width, func_width
      @file_width     = file_width
      @line_width     = line_width
      @function_width = func_width
      
      @format = "[%#{file_width}s:%#{line_width}d] {%#{func_width}s}"
    end

    def ignore_file fname
      ignored_files[fname] = true
    end
    
    def ignore_method methname
      ignored_methods[methname] = true
    end
    
    def ignore_class classname
      ignored_classes[classname] = true
    end

    def log_file fname
      ignored_files.delete fname
    end
    
    def log_method methname
      ignored_methods.delete methname
    end
    
    def log_class classname
      ignored_classes.delete classname
    end

    def debug msg = "", depth = 1, &blk
      log msg, DEBUG, depth + 1, &blk
    end

    def info msg = "", depth = 1, &blk
      log msg, INFO, depth + 1, &blk
    end

    def warn msg = "", depth = 1, &blk
      log msg, WARN, depth + 1, &blk
    end

    def error msg = "", depth = 1, &blk
      log msg, ERROR, depth + 1, &blk
    end

    def fatal msg = "", depth = 1, &blk
      log msg, FATAL, depth + 1, &blk
    end

    # Logs the given message.
    def log msg = "", lvl = DEBUG, depth = 1, cname = nil, &blk
      if lvl >= level
        frame = nil

        stk = caller 0
        stk.reverse.each_with_index do |frm, idx|
          if frm.index(%r{/riel/log.rb:\d+:in\b})
            break
          else
            frame = frm
          end
        end

        print_stack_frame frame, cname, msg, lvl, &blk
      end
    end

    # Shows the current stack.
    def stack msg = "", lvl = DEBUG, depth = 1, cname = nil, &blk
      if lvl >= level
        stk = caller depth
        for frame in stk
          print_stack_frame frame, cname, msg, lvl, &blk
          msg = ""
        end
      end
    end

    def print_stack_frame frame, cname, msg, lvl, &blk
      md = FRAME_RE.match frame
      file, line, func = md[1], md[2], (md[3] || "")
      file.sub!(/.*\//, "")

      if cname
        func = cname + "#" + func
      end
      
      if ignored_files[file] || (cname && ignored_classes[cname]) || ignored_methods[func]
        # skip this one.
      else
        print_formatted(file, line, func, msg, lvl, &blk)
      end
    end

    def trim_to str, maxlen
      str[0 ... maxlen.to_i.abs]
    end

    def print_formatted file, line, func, msg, lvl, &blk
      if trim
        file = trim_to file, @file_width
        line = trim_to line, @line_width
        func = trim_to func, @function_width
      end

      hdr = sprintf @format, file, line, func
      print hdr, msg, lvl, &blk
    end
    
    def print hdr, msg, lvl, &blk
      if blk
        x = blk.call
        if x.kind_of? String
          msg = x
        else
          return
        end
      end

      if @colors[lvl]
        if colorize_line
          @output.puts @colors[lvl] + hdr + " " + msg.to_s.chomp + ANSIColor.reset
        else
          @output.puts hdr + " " + @colors[lvl] + msg.to_s.chomp + ANSIColor.reset
        end
      else
        @output.puts hdr + " " + msg.to_s.chomp
      end      
    end

    def set_color lvl, color
      @colors[lvl] = ANSIColor::code(color)
    end

    # by default, class methods delegate to a single app-wide log.

    @@log = Log.new

    # Returns the logger of the log. A class method delegating to an instance
    # method ... not so good. But temporary.
    def self.logger
      @@log
    end

    def self.method_missing(meth, *args, &blk)
      if code = ANSIColor::ATTRIBUTES[meth.to_s]
        add_color_method meth.to_s, code
        send meth, *args, &blk
      else
        super
      end
    end

    def self.add_color_method color, code
      instmeth = Array.new
      instmeth << "def #{color}(msg = \"\", lvl = DEBUG, depth = 1, cname = nil, &blk)"
      instmeth << "  log(\"\\e[#{code}m\#{msg\}\\e[0m\", lvl, depth + 1, cname, &blk)"
      instmeth << "end"
      instance_eval instmeth.join("\n")

      clsmeth = Array.new
      clsmeth << "def #{color}(msg = \"\", lvl = DEBUG, depth = 1, cname = nil, &blk)"
      clsmeth << "  logger.#{color}(\"\\e[#{code}m\#{msg\}\\e[0m\", lvl, depth + 1, cname, &blk)"
      clsmeth << "end"

      class_eval clsmeth.join("\n")
    end

    if false
      ANSIColor::ATTRIBUTES.sort.each do |attr|
        methname = attr[0]

        instmeth = Array.new
        instmeth << "def #{methname}(msg = \"\", lvl = DEBUG, depth = 1, cname = nil, &blk)"
        instmeth << "  log(\"\\e[#{attr[1]}m\#{msg\}\\e[0m\", lvl, depth + 1, cname, &blk)"
        instmeth << "end"
        instance_eval instmeth.join("\n")

        clsmeth = Array.new
        clsmeth << "def #{methname}(msg = \"\", lvl = DEBUG, depth = 1, cname = nil, &blk)"
        clsmeth << "  logger.#{methname}(\"\\e[#{attr[1]}m\#{msg\}\\e[0m\", lvl, depth + 1, cname, &blk)"
        clsmeth << "end"

        class_eval clsmeth.join("\n")
      end
    end

    def self.set_default_widths
      logger.set_default_widths
    end

    def self.verbose
      logger.verbose
    end

    def self.verbose= v
      logger.verbose = v && v != 0 ? DEBUG : FATAL
    end

    def self.level= lvl
      logger.level = lvl
    end

    def self.quiet
      logger.quiet
    end

    def self.quiet= q
      logger.quiet = q
    end

    def self.format
      logger.format
    end

    def self.format= fmt
      logger.format = fmt
    end

    # Assigns output to the given stream.
    def self.output= io
      logger.output = io
    end

    def self.output
      logger.output
    end

    # sets whether to colorize the entire line, or just the message.
    def self.colorize_line= col
      logger.colorize_line = col
    end

    def self.colorize_line
      logger.colorize_line
    end

    # Assigns output to a file with the given name. Returns the file; client
    # is responsible for closing it.
    def self.outfile= fname
      logger.outfile = fname
    end

    def self.outfile
      logger.outfile
    end

    # Creates a printf format for the given widths, for aligning output.
    def self.set_widths file_width, line_width, func_width
      logger.set_widths file_width, line_width, func_width
    end

    def self.ignore_file fname
      logger.ignore_file fname
    end
    
    def self.ignore_method methname
      logger.ignored_method methname
    end
    
    def self.ignore_class classname
      logger.ignored_class classname
    end

    def self.log_file fname
      logger.log_file fname
    end
    
    def self.log_method methname
      logger.log_method methname
    end
    
    def self.log_class classname
      logger.log_class classname
    end

    def self.debug msg = "", depth = 1, &blk
      logger.log msg, DEBUG, depth + 1, &blk
    end

    def self.info msg = "", depth = 1, &blk
      logger.log msg, INFO, depth + 1, &blk
    end

    def self.fatal msg = "", depth = 1, &blk
      logger.log msg, FATAL, depth + 1, &blk
    end

    def self.log msg = "", lvl = DEBUG, depth = 1, cname = nil, &blk
      logger.log msg, lvl, depth + 1, cname, &blk
    end

    def self.stack msg = "", lvl = DEBUG, depth = 1, cname = nil, &blk
      logger.stack msg, lvl, depth + 1, cname, &blk
    end

    def self.warn msg = "", depth = 1, &blk
      if verbose
        logger.log msg, WARN, depth + 1, &blk
      else
        $stderr.puts "WARNING: " + msg
      end
    end

    def self.error msg = "", depth = 1, &blk
      if verbose
        logger.log msg, ERROR, depth + 1, &blk
      else
        $stderr.puts "ERROR: " + msg
      end
    end

    def self.write msg, depth = 1, cname = nil, &blk
      if verbose
        stack msg, Log::WARN, depth + 1, cname, &blk
      elsif quiet
        # nothing
      else
        $stderr.puts msg
      end
    end

    def self.set_color lvl, color
      logger.set_color lvl, color
    end

  end

  class AppLog < Log
    include Log::Severity
  end

  module Loggable
    # Logs the given message, including the class whence invoked.
    def log msg = "", lvl = Log::DEBUG, depth = 1, &blk
      Log.log msg, lvl, depth + 1, self.class.to_s, &blk
    end

    def debug msg = "", depth = 1, &blk
      Log.log msg, Log::DEBUG, depth + 1, self.class.to_s, &blk
    end

    def info msg = "", depth = 1, &blk
      Log.log msg, Log::INFO, depth + 1, self.class.to_s, &blk
    end

    def warn msg = "", depth = 1, &blk
      Log.log msg, Log::WARN, depth + 1, self.class.to_s, &blk
    end

    def error msg = "", depth = 1, &blk
      Log.log msg, Log::ERROR, depth + 1, self.class.to_s, &blk
    end

    def fatal msg = "", depth = 1, &blk
      Log.log msg, Log::FATAL, depth + 1, self.class.to_s, &blk
    end

    def stack msg = "", lvl = Log::DEBUG, depth = 1, &blk
      Log.stack msg, lvl, depth + 1, self.class.to_s, &blk
    end

    def write msg = "", depth = 1, &blk
      Log.write msg, depth + 1, self.class.to_s, &blk
    end

    def method_missing(meth, *args, &blk)
      if ANSIColor::ATTRIBUTES[meth.to_s]
        add_color_method meth.to_s
        send meth, *args, &blk
      else
        super
      end
    end

    def add_color_method color
      meth = Array.new
      meth << "def #{color} msg = \"\", lvl = Log::DEBUG, depth = 1, &blk"
      meth << "  Log.#{color} msg, lvl, depth + 1, self.class.to_s, &blk"
      meth << "end"
      self.class.module_eval meth.join("\n")
    end

    if false
      ANSIColor::ATTRIBUTES.sort.each do |attr|
        methname = attr[0]
        meth = Array.new
        meth << "def #{methname} msg = \"\", lvl = Log::DEBUG, depth = 1, &blk"
        meth << "  Log.#{methname} msg, lvl, depth + 1, self.class.to_s, &blk"
        meth << "end"
        module_eval meth.join("\n")
      end
    end
  end
end

include RIEL

if __FILE__ == $0
  Log.verbose = true
  Log.set_widths 15, -5, -35
  #Log.outfile = "/tmp/log." + $$.to_s

  class Demo
    include Loggable
    
    def initialize
      # log "hello"
      Log.set_color Log::DEBUG, "cyan"
      Log.set_color Log::INFO,  "bold cyan"
      Log.set_color Log::WARN,  "reverse"
      Log.set_color Log::ERROR, "bold red"
      Log.set_color Log::FATAL, "bold white on red"
    end

    def meth
      # log

      i = 4
      # info { "i: #{i}" }

      i /= 3
      debug { "i: #{i}" }

      i **= 3
      info "i: #{i}"

      i **= 2
      warn "i: #{i}"

      i <<= 4
      error "i: #{i}"

      i <<= 1
      fatal "i: #{i}"
    end
  end

  class Another
    include Loggable
    
    def Another.cmeth
      # /// "Log" only in instance methods
      # log "I'm sorry, Dave, I'm afraid I can't do that."

      # But this is legal.
      Log.log "happy, happy, joy, joy"
    end
  end
  
  demo = Demo.new
  demo.meth

  # Log.colorize_line = true

  # demo.meth
  # Another.cmeth

  # Log.info "we are done."
end
