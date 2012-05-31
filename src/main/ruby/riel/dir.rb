#!/usr/bin/ruby -w
# -*- ruby -*-

require 'pathname'

class Dir

  # Returns the home directory, for both Unix and Windows.
  def self.home
    ENV["HOME"] || begin
                     hd = ENV["HOMEDRIVE"]
                     hp = ENV["HOMEPATH"]
                     if hd || hp
                       (hd || "") + (hp || "\\")
                     end
                   end
  end

  # Removes directories containing no files or files matching only those in
  # args[:deletable_files], which are basenames.
  def self.remove_if_empty(dir, args = Hash.new)
    deletable = args[:deletable] || Array.new
    verbose   = args[:verbose]
    level     = args[:level] || 0

    subargs = args.dup
    subargs[:level] = level + 1

    dir = Pathname.new(dir) unless dir.kind_of?(Pathname)

    if level <= 1 && verbose
      puts "dir: #{dir}"
    end

    if dir.readable?
      dir.children.sort.each do |child|
        if child.exist? && child.directory?
          self.remove_if_empty(child, subargs)
        end
      end
      
      if dir.expand_path == Pathname.pwd.expand_path
        puts "skipping current directory: #{dir}" if verbose
      else
        can_delete = dir.children.all? do |x| 
          bname = x.basename.to_s
          deletable.any? do |del|
            if del.kind_of?(String)
              bname == del
            elsif del.kind_of?(Regexp)
              del.match(bname)
            else
              false
            end
          end
        end

        if can_delete
          dir.children.each do |x|
            puts "removing file: #{x}" if verbose
            x.delete
          end

          puts "removing directory: #{dir}" if verbose

          dir.delete
        else
          # puts "#{dir} not empty"
        end
      end
    elsif verbose
      puts "#{dir} not readable"
    end
  end

  # Moves and copies files to the given directory, creating
  # it if it does not exist.
  def self.move_and_copy_files(dir, move_files, copy_files)
    dir.mkdir unless dir.exist?

    move_files.each do |mfile|
      File.move(mfile.to_s, dir.to_s)
    end

    copy_files.each do |cfile|
      File.copy(cfile.to_s, dir.to_s)
    end
  end

end
