#!/usr/bin/ruby -w
# -*- ruby -*-

require 'pathname'
require 'riel/string'
require 'ftools'

class Pathname

  # a compliment to the +dirname+, +basename+, and +extname+ family, this returns
  # the basename without the extension, e.g. "foo" from "/usr/share/lib/foo.bar".
  def rootname
    basename.to_s - extname.to_s
  end

  # Returns an array of the path split into its components.
  # for example: "/usr/bin/ls" => "usr", "bin", "ls"
  def split_path
    elements = Array.new
    each_filename { |fn| elements << fn.to_s }
    elements
  end

  def mkdirs
    File.makedirs to_s
  end

end
