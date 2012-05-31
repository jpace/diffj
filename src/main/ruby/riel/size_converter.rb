#!/usr/bin/ruby -w
# -*- ruby -*-

class SizeConverter
  # http://www.gnu.org/software/coreutils/manual/html_node/Block-size.html

  # don't round to closest -- just convert
  def self.convert_to_kilobytes(size, decimal_places = 1)
    ### SizeConverter._convert(Human::CONVERSIONS, 2, size, decimal_places)
  end

  class Human
    CONVERSIONS = [ 
      [ 12, "T" ],
      [  9, "G" ],
      [  6, "M" ],
      [  3, "K" ]
    ]

    # returns a string representation of the size. Note that K, G, M are
    # gibibytes, etc., that is, powers of 10.
    
    def self.convert(size, decimal_places = 1)
      SizeConverter._convert(CONVERSIONS, 10, size, decimal_places)
    end
  end

  class SI
    # http://physics.nist.gov/cuu/Units/binary.html
    CONVERSIONS = [ 
      [ 40, "TiB" ],
      [ 30, "GiB" ],
      [ 20, "MiB" ],
      [ 10, "KiB" ]
    ]

    # returns a string representation of the size. Note that K, G, M are
    # gigabytes, etc.
    
    def self.convert(size, decimal_places = 1)
      SizeConverter._convert(CONVERSIONS, 2, size, decimal_places)
    end
  end

  # legacy:

  def self.convert(size, decimal_places = 1)
    Human::convert(size, decimal_places)
  end

  def self._convert(conversions, base, size, decimal_places)
    sizef = size.to_f
    conversions.each do |conv|
      sz = sizef / (base ** conv[0])
      if sz >= 1.0
        return sprintf("%.*f%s", decimal_places, sz, conv[1])
      end
    end

    sprintf("%.*f", decimal_places, size)
  end
end
