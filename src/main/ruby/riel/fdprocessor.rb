#!/usr/bin/ruby -w
# -*- ruby -*-

module RIEL
  # File and directory processor, with filtering.

  class FileDirFilter
    def initialize args
      @dirsonly = args[:dirsonly]
      @filesonly = args[:filesonly]
      @basename = args[:basename]
      @dirname = args[:dirname]
      @extname = args[:extname]
    end

    def match? fd
      if @dirsonly && !fd.directory?
        false
      elsif @filesonly && !fd.file?
        false
      elsif @basename && @basename != fd.basename.to_s
        false
      elsif @dirname && @dirname != fd.parent.to_s
        false
      elsif @extname && @extname != fd.extname
        false
      else
        true
      end
    end
  end

  class FileDirProcessor
    def initialize args, filters = Array.new
      @filters = filters
      args.each do |arg|
        process Pathname.new arg
      end
    end

    def process_file file
    end

    def process_directory dir
      dir.children.sort.each do |fd|
        next if @filter && @filter.include?(fd.basename.to_s)
        process fd
      end
    end

    def process fd
      @filters.each do |filter| 
        return nil unless filter.match? fd
      end
      
      if fd.directory?
        process_directory fd
      elsif fd.file?
        process_file fd
      else
        process_unknown_type fd
      end
    end

    def process_unknown_type fd
    end
  end
end
