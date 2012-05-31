#!/usr/bin/ruby -w
# -*- ruby -*-

module Text

  # Highlights text using either ANSI terminal codes, or HTML.

  # Note that the foreground and background sections can have modifiers
  # (attributes).
  # 
  # Examples:
  #     black
  #     blue on white
  #     bold green on yellow
  #     underscore bold magenta on cyan
  #     underscore red on cyan
  
  class Highlighter

    VERSION = "1.0.4"

    ATTRIBUTES = %w{
      none
      reset
      bold
      underscore
      underline
      blink
      negative
      concealed
      black
      red
      green
      yellow
      blue
      magenta
      cyan
      white
      on_black
      on_red
      on_green
      on_yellow
      on_blue
      on_magenta
      on_cyan
      on_white
    }
    
    NONE = Object.new
    HTML = Object.new
    ANSI = Object.new

    COLORS      = %w{ black red green yellow blue magenta cyan white }
    DECORATIONS = %w{ none reset bold underscore underline blink negative concealed }

    BACKGROUND_COLORS = COLORS.collect { |color| "on_#{color}" }
    FOREGROUND_COLORS = COLORS

    COLORS_RE = Regexp.new('(?: ' + 
                                # background will be in capture 0
                                'on(?:\s+|_) ( ' + COLORS.join(' | ') + ' ) | ' +
                                # foreground will be in capture 1
                                '( ' + (COLORS + DECORATIONS).join(' | ') + ' ) ' +
                            ')', Regexp::EXTENDED);

    DEFAULT_COLORS = [
      "black on yellow",
      "black on green",
      "black on magenta",
      "yellow on black",
      "magenta on black",
      "green on black",
      "cyan on black",
      "blue on yellow",
      "blue on magenta",
      "blue on green",
      "blue on cyan",
      "yellow on blue",
      "magenta on blue",
      "green on blue",
      "cyan on blue",
    ]

    attr_reader :colors
    
    def self.parse_colors str
      str.scan(Regexp.new(COLORS_RE)).collect do |color|
        color[0] ? "on_" + color[0] : color[1]
      end
    end

    # returns a list of all color combinations.
    def self.all_colors
      all_colors = Array.new
      ([ nil ] + DECORATIONS).each do |dec|
        ([ nil ] + FOREGROUND_COLORS).each do |fg|
          ([ nil ] + BACKGROUND_COLORS).each do |bg|
            name = [ dec, fg, bg ].compact.join("_")
            all_colors << name if name && name.length > 0
          end
        end
      end
      all_colors
    end

    # todo: change this to use method_missing:
    if false
      all_colors.each do |name|
        meth = Array.new
        meth << "def #{name}(&blk)"
        meth << "  color(\"#{name}\", &blk)"
        meth << "end"

        self.class_eval meth.join("\n")
      end
    end
    
    def initialize colors
      @colors = colors
    end

    def highlight str
      # implemented by subclasses
    end

    def to_s
      (@colors || '').join(' ')
    end

    def == other
      return @colors.sort == other.colors.sort
    end

    # Colorizes the given object. If a block is passed, its return value is used
    # and the stream is reset. If a String is provided as the object, it is
    # colorized and the stream is reset. Otherwise, only the code for the given
    # color name is returned.
    
    def color colorstr, obj = self, &blk
      #                       ^^^^ this is the Module self

      colornames = self.class.parse_colors(colorstr)
      result     = names_to_code(colornames)
      
      if blk
        result << blk.call
        result << names_to_code("reset")
      elsif obj.kind_of?(String)
        result << obj
        result << names_to_code("reset")
      end
      result
    end

    # returns the code for the given color string, which is in the format:
    # foreground* [on background]?
    # 
    # Note that the foreground and background sections can have modifiers
    # (attributes).
    # 
    # Examples:
    #     black
    #     blue on white
    #     bold green on yellow
    #     underscore bold magenta on cyan
    #     underscore red on cyan

    def code str
      fg, bg = str.split(/\s*\bon_?\s*/)
      (fg ? foreground(fg) : "") + (bg ? background(bg) : "")
    end

    # Returns the code for the given background color(s).
    def background bgcolor
      names_to_code "on_" + bgcolor
    end

    # Returns the code for the given foreground color(s).
    def foreground fgcolor
      fgcolor.split(/\s+/).collect { |fg| names_to_code fg }.join("")
    end

  end

  # Highlights using ANSI escape sequences.
  class ANSIHighlighter < Highlighter
    ATTRIBUTES = Hash[
      'none'       => '0', 
      'reset'      => '0',
      'bold'       => '1',
      'underscore' => '4',
      'underline'  => '4',
      'blink'      => '5',
      'negative'   => '7',
      'concealed'  => '8',
      'black'      => '30',
      'red'        => '31',
      'green'      => '32',
      'yellow'     => '33',
      'blue'       => '34',
      'magenta'    => '35',
      'cyan'       => '36',
      'white'      => '37',
      'on_black'   => '40',
      'on_red'     => '41',
      'on_green'   => '42',
      'on_yellow'  => '43',
      'on_blue'    => '44',
      'on_magenta' => '45',
      'on_cyan'    => '46',
      'on_white'   => '47',
    ]

    RESET = "\e[0m"

    def self.make str
      colors = parse_colors str
      ANSIHighlighter.new colors
    end

    def initialize colors = DEFAULT_COLORS
      super
      @code = nil
    end

    # Returns the escape sequence for the given names.
    def names_to_code names
      str = ""
      names.each do |name|
        code = ATTRIBUTES[name]
        if code
          str << "\e[#{code}m"
        end
      end
      str
    end

    def highlight str
      @code ||= begin
                  @code = @colors.collect do |color|
                    names_to_code color
                  end.join ""
                end
      
      @code + str + RESET
    end

  end

  # Highlights using HTML. Fonts are highlighted using <span> tags, not <font>.
  # Also note that negative is translated to white on black.
  # According to http://www.w3.org/TR/REC-CSS2/syndata.html#value-def-color,
  # valid color keywords are: aqua, black, blue, fuchsia, gray, green, lime,
  # maroon, navy, olive, purple, red, silver, teal, white, and yellow.
  # Thus, no magenta or cyan.

  class HTMLHighlighter < Highlighter
    def initialize
      # we need to know what we're resetting from (bold, font, underlined ...)
      @stack = []
    end

    # Returns the start tag for the given name.
    
    def start_style name
      case name
      when "negative"
        "<span style=\"color: white; background-color: black\">"
      when /on_(\w+)/
        colval = color_value($1)
        "<span style=\"background-color: #{colval}\">"
      else
        colval = color_value(name)
        "<span style=\"color: #{colval}\">"
      end
    end

    # Returns the end tag ("</span>").

    def end_style
      "</span>"
    end

    def color_value cname
      case cname
      when "cyan"
        "#00FFFF"
      when "magenta"
        "#FF00FF"
      else
        cname
      end
    end

    # Returns the code for the given name.
    def names_to_code names
      str = ""

      names.each do |name|
        @stack << name

        case name
        when "none", "reset"
          @stack.pop
          if @stack.length > 0
            begin
              prev = @stack.pop
              case prev
              when "bold"
                str << "</b>"
              when "underscore", "underline"
                str << "</u>"
              when "blink"
                str << "</blink>"
              when "concealed"
                str << " -->"
              else
                str << end_style
              end
            end while @stack.length > 0
          end
          str
        when "bold"
          str << "<b>"
        when "underscore", "underline"
          str << "<u>"
        when "blink"
          str << "<blink>"
        when "concealed"
          str << "<!-- "
        else
          str << start_style(name)
        end
      end

      str
    end
  end

  # Does no highlighting.

  class NonHighlighter < Highlighter
    def initialize
      super nil
    end

    # Since the NonHighlighter does no highlighting, and thus its name, this
    # returns an empty string.
    def names_to_code colorname
      ""
    end
  end

  # An object that can be highlighted. This is used by the String class.

  module Highlightable
    # The highlighter for the class in which this module is included.
    @@highlighter = ANSIHighlighter.new(Text::Highlighter::DEFAULT_COLORS)

    if false
      Text::Highlighter::ATTRIBUTES.each do |name|
        meth = Array.new
        meth << "def #{name}(&blk)"
        meth << "  @@highlighter.color(\"#{name}\", self, &blk)"
        meth << "end"

        self.class_eval meth.join("\n")
      end
    end

    # this dynamically adds methods for individual colors.
    def method_missing(meth, *args, &blk)
      if Text::Highlighter::all_colors.include? meth.to_s
        methdecl = Array.new
        methdecl << "def #{meth}(&blk)"
        methdecl << "  @@highlighter.color(\"#{meth}\", self, &blk)"
        methdecl << "end"
        self.class.class_eval methdecl.join("\n")
        send meth, *args, &blk
      else
        super
      end
    end

    # Sets the highlighter for this class. This can be either by type or by
    # String.
    def highlighter= hl
      $VERBOSE = false
      @@highlighter = case hl
                      when Text::Highlighter
                        hl
                      when Text::Highlighter::NONE, "NONE", nil
                        Text::NonHighlighter.new #  unless @@highlighter.kind_of?(Text::NonHighlighter)
                      when Text::Highlighter::HTML, "HTML"
                        Text::HTMLHighlighter.new # unless @@highlighter.kind_of?(Text::HTMLHighlighter)
                      when Text::Highlighter::ANSI, "ANSI"
                        Text::ANSIHighlighter.new
                      else
                        Text::NonHighlighter.new
                      end
      
    end
  end
  $HAVE_TEXT_HIGHLIGHT = true
end

# String is extended to support highlighting.

class String
  include Text::Highlightable
  extend Text::Highlightable
end
