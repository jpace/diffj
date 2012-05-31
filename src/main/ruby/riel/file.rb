#!/usr/bin/ruby -w
# -*- ruby -*-

require 'riel/filetype'
require 'riel/pathname'
require 'riel/tempfile'
require 'fileutils'

# this eliminates the world-writable warning when running file operations
# (opening, finding, executing):

orig_verbose = $VERBOSE
$VERBOSE = nil
path = ENV['PATH'].dup
$VERBOSE = orig_verbose

class File

  def self.text?(file)
    return File.file?(file) && FileType.instance.text?(file)
  end

  def self.binary?(file)
    return !self.text?(file)
  end

  # Returns whether the given object is a file. Ignores errors.
  def self.is_file?(fd)
    begin 
      return self.stat(fd).file?
    rescue
      # ignore files that could not be read, etc.
      return false
    end
  end
  
  # Returns whether the given file is writable. Ignores errors.
  def self.is_writable?(file)
    begin 
      return self.stat(file).writable?
    rescue
      # ignore files that could not be read, etc.
      return false
    end
  end
  
  # Returns whether the given object is a directory. Ignores errors.
  def self.is_directory?(fd)
    begin 
      return self.stat(fd).directory?
    rescue
      # ignore files that could not be read, etc.
      return false
    end
  end

  # Returns an array of all files under the given directory.
  def self.find_files(dir)
    files = Array.new
    Find.find(dir) { |f| files.push(f) if is_file?(f) }
    files
  end

  # Returns an array of all directory under the given directory.
  def self.find_directories(dir)
    dirs = Array.new
    Find.find(dir) { |d| dirs.push(d) if is_directory?(d) }
    dirs
  end

  # Removes the file/directory, including all subelements. Use with caution!
  def self.remove_recursively(fd)
    #$$$ this is rmtree
    if fd.directory?
      fd.children.each { |x| remove_recursively(x) }
    end
    fd.unlink
  end

  # Creates the given directory.
  def self.mkdir(dir)
    pn = Pathname.new(dir)
    pn.mkdir unless pn.exist?
  end

  # Moves the files to the given directory, creating it if it does not exist.
  def self.move_files(dir, files)
    mkdir(dir)

    files.each do |file|
      FileUtils.move(file.to_s, dir.to_s)
    end
  end

  # Copies the files to the given directory, creating it if it does not exist.
  def self.copy_files(dir, files)
    mkdir(dir)

    files.each do |file|
      FileUtils.copy(file.to_s, dir.to_s)
    end
  end

  # Converts the argument to a Pathname.
  def self._to_pathname(file)
    file.kind_of?(Pathname) ? file : Pathname.new(file.to_s)
  end

  # Reads a file line by line. Returns the pathname for the file, or nil if it
  # does not exist.
  def self.read_file(file, &blk)
    fpn = _to_pathname(file)
    if fpn.exist?
      fpn.open do |f|
        blk.call f.read
      end
      fpn
    else
      nil
    end
  end

  # Reads a file line by line, calling the given block. Returns the pathname for
  # the file, or nil if it does not exist.
  def self.read_file_lines(file, &blk)
    fpn = _to_pathname(file)
    if fpn.exist?
      fpn.open do |f|
        f.each_line do |line|
          blk.call line
        end
        fpn
      end
    else
      nil
    end
  end

  # Opens a file for writing and delegates to the given block.
  def self.open_writable_file(file, &blk)
    fpn = _to_pathname(file)
    
    fpn.open(File::WRONLY | File::TRUNC | File::CREAT) do |f|
      blk.call(f)
    end

    fpn
  end

  # Writes a file, using the <code>write</code> method.
  def self.write_file(file, &blk)
    open_writable_file(file) do |io|
      io.write blk.call
    end
  end

  # Writes a file, using puts (thus making it better for text files).
  def self.put_file(file, &blk)
    open_writable_file(file) do |io|
      io.puts blk.call
    end
  end

  # Opens a tempfile for writing, delegates to the given block, and renames the
  # temp file to <code>file</code>. If <code>tempfile</code> is specified, it
  # will be used as the temp file name. Ditto for <code>tempdir</code>, which
  # defaults to Dir::tmpdir (e.g. "/tmp")

  def self.open_via_temp_file(file, tempfile = nil, tempdir = Dir::tmpdir, &blk)
    tempname = nil
    
    fpn = _to_pathname(file)
    tempfile ||= fpn.rootname

    Tempfile.open(tempfile) do |tf|
      blk.call(tf)
      tempname = tf.path
    end
    
    FileUtils.mv(tempname, file.to_s)
  end

  # Writes a file, using write, buffering it via a temp file.
  def self.write_via_temp_file(file, &blk)
    open_via_temp_file(file) do |io|
      io.write blk.call
    end
  end

  # Writes a file, using puts, buffering it via a temp file.
  def self.put_via_temp_file(file, &blk)
    open_via_temp_file(file) do |io|
      io.puts blk.call
    end
  end

  # Returns a file for the given basename, sequentially appending an integer
  # until one is found that does not exist. For example, "foo.3" if "foo",
  # "foo.1", and "foo.2" already exist.
  def self.get_unused_file_name(basename)
    tgt = basename
    if tgt.exist?
      i = 1
      while tgt.exist?
        tgt = Pathname.new(basename.to_s + "." + i.to_s)
        i += 1
      end
    end
    tgt
  end
  
end
