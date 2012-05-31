#!/usr/bin/ruby -w
# -*- ruby -*-

require 'singleton'

class FileType
  include Singleton

  BINARY     = "binary"
  DIRECTORY  = "directory"
  NONE       = "none"
  TEXT       = "text"
  UNKNOWN    = "unknown"
  UNREADABLE = "unreadable"

  # extensions associated with files that are always text:
  TEXT_EXTENSIONS = %w{ 
    c
    css
    el
    h
    html
    java
    mk
    php
    pl
    pm
    rb
    rbw
    txt
    xml
    yml
    yaml
  }

  # extensions associated with files that are never text:
  NONTEXT_EXTENSIONS = %w{ 
    Z
    a
    bz2
    elc
    gif
    gz
    jar
    jpeg
    jpg
    mp3
    mpeg
    o
    obj
    pdf
    png
    ps
    tar
    wav
    zip
  }

  EXTENSION_REGEXP = %r{ \. (\w+) $ }x

  def initialize
    # the percentage of characters that we allow to be odd in a text file
    @odd_factor = 0.3

    # how many bytes (characters) of a file we test
    @test_length = 1024

    @known = Hash.new

    set_extensions(true,  *TEXT_EXTENSIONS)
    set_extensions(false, *NONTEXT_EXTENSIONS)
  end

  def ascii?(c)
    # from ctype.h
    (c.to_i & ~0x7f) == 0
  end

  def type(file)
    begin
      case File.stat(file).ftype
      when "directory"
        DIRECTORY
      when "file"
        if File.readable?(file)
          text?(file) ? TEXT : BINARY
        else
          UNREADABLE
        end
      else
        UNKNOWN
      end
    rescue Errno::ENOENT
      NONE
    rescue => e
      warn "file not readable: #{file}; error: #{e}"
      UNREADABLE
    end
  end

  def set_extensions(is_text, *exts)
    exts.each do |ext|
      @known[ext] = is_text
    end
  end

  def set_text(*ext)
    @known[ext] = true
  end

  def set_nontext(ext)
    @known[ext] = false
  end

  def text_extensions
    @known.keys.select { |suf| @known[suf] }
  end

  def nontext_extensions
    @known.keys.reject { |suf| @known[suf] }
  end

  def text?(file)
    return false unless File.exists?(file)
    
    if md = EXTENSION_REGEXP.match(file.to_s)
      suffix = md[1]
      if @known.include?(suffix)
        return @known[suffix]
      end
    end
    
    ntested = 0
    nodd = 0

    begin
      File.open(file) do |f|
        buf = f.read(@test_length)
        if buf
          buf.each_byte do |ch|
            ntested += 1
            
            # never allow null in a text file
            return false if ch.to_i == 0
            
            nodd += 1 unless ascii?(ch)
          end
        else
          # file had length of 0:
          return UNKNOWN
        end
      end
    rescue => e
      warn "file not readable: #{file}; error: #{e}"
      return UNREADABLE
    end

    nodd < ntested * @odd_factor
  end

  def self.ascii?(c)
    return self.instance.ascii?(c)
  end

  def self.type(file)
    return self.instance.type(file)
  end

  def self.set_text(ext)
    return self.instance.set_text(ext)
  end

  def self.set_nontext(ext)
    return self.instance.set_nontext(ext)
  end

  def self.text_extensions
    return self.instance.text_extensions
  end

  def self.nontext_extensions
    return self.instance.nontext_extensions
  end

  def self.text?(file)
    return self.instance.text?(file)
  end

end
