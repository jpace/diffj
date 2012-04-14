#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  module IO
    # location within a file (line and column)
    class Location < org.incava.ijdk.text.Location
      include Loggable      

      class << self
        def beginning tk
          tk && new(tk.beginLine, tk.beginColumn)
        end
    
        def ending tk
          tk && new(tk.endLine, tk.endColumn)
        end
      end
    end

    class LocationRange < org.incava.ijdk.text.LocationRange
      include Loggable

    end
  end
end

__END__


package org.incava.ijdk.text;

/**
 * Code location.
 */
public class Location implements Comparable<Location> {
    public final int line;    
    public final int column;
    
    public Location(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public String toString() {
        return "" + line + ":" + column;
    }

    public boolean equals(Object obj) {
        return obj instanceof Location && equals((Location)obj);
    }

    public boolean equals(Location other) {
        return compareTo(other) == 0;
    }

    public int compareTo(Location other) {
        int cmp = new Integer(this.line).compareTo(other.getLine());
        if (cmp == 0) {
            cmp = new Integer(this.column).compareTo(other.getColumn());
        }
        return cmp;
    }
}
