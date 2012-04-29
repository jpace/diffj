#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast'
require 'diffj/io/directory'
require 'diffj/io/file'
require 'diffj/util/exception'

include Java

module DiffJ
  module IO
    class Factory
      include Loggable
      
      def create_element file, label, source, recurse
        javafile = create_file file, label, source
        if javafile
          javafile
        elsif file.directory?
          Directory.new file, source, recurse
        else
          no_such_file file, label
          nil
        end
      end

      def create_file file, label, source
        if file.nil? || file.to_s == "-" || (file.file? && verify_exists(file, label))
          File.new file, label, nil, source
        else
          info "cannot create file: #{file}".red
          nil
        end
      end

      def verify_exists file, label
        pn = file && Pathname.new(file.to_s)
        (pn && pn.exist?) || no_such_file(file, label)
      end

      def no_such_file file, label
        info "file: #{file}; #{label}".red
        raise DiffJ::Exception.new name(file, label) + " does not exist"
      end

      def name file, label
        label || file.absolute_path
      end
    end
  end
end
