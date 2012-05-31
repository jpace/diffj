#!/usr/bin/ruby -w
# -*- ruby -*-

module Env

  # Returns the home directory, for both Unix and Windows.

  def self.home_directory
    if hm = ENV["HOME"]
      hm
    else
      hd = ENV["HOMEDRIVE"]
      hp = ENV["HOMEPATH"]
      if hd || hp
        (hd || "") + (hp || "\\")
      else
        nil
      end
    end
  end

  # matches single and double quoted strings:
  REGEXP = /                    # either:
              ([\"\'])          #     start with a quote, and save it ($1)
              (                 #     save this ($2)
                (?:             #         either (and don't save this):
                    \\.         #             any escaped character
                  |             #         or
                    [^\1\\]     #             anything that is not a quote ($1), and is not a backslash
                )*?             #         only up to the next quote
              )                 #         end of $2
              \1                #     end with the same quote we started with
            |                   # or
              (:?\S+)           #     plain old nonwhitespace
           /x
      
  # amazing that ruby-mode (Emacs) handled all that.
  
  # Reads the environment variable, splitting it according to its quoting.

  def self.split(varname)
    if v = ENV[varname]
      v.scan(REGEXP).collect { |x| x[1] || x[2] }
    else
      []
    end
  end

end
