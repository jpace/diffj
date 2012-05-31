#!/usr/bin/ruby -w
# -*- ruby -*-

#     Attribute codes:
#         00=none 01=bold 04=underscore 05=blink 07=reverse 08=concealed
#     Text color codes:
#         30=black 31=red 32=green 33=yellow 34=blue 35=magenta 36=cyan 37=white
#     Background color codes:
#         40=black 41=red 42=green 43=yellow 44=blue 45=magenta 46=cyan 47=white

module ANSIColor
  ATTRIBUTES = Hash[
    'none'       => '0', 
    'reset'      => '0',
    'bold'       => '1',
    'underscore' => '4',
    'underline'  => '4',
    'blink'      => '5',
    'reverse'    => '7',
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

  ATTRIBUTES.each do |name, val|
    eval <<-EODEF
      def ANSIColor.#{name}
        "\\e[#{val}m"
      end
    EODEF
  end

  def ANSIColor.attributes
    ATTRIBUTES.collect { |name, val| name }
  end
  
  # returns the code for the given color string, which is in the format:
  # [foreground] on [background]. Note that the foreground and background sections
  # can have modifiers (attributes). Examples:
  #     black
  #     blue on white
  #     bold green on yellow
  #     underscore bold magenta on cyan
  #     underscore red on bold cyan

  def ANSIColor.code str
    fg, bg = str.split(/\s*\bon_?\s*/)
    (fg ? foreground(fg) : "") + (bg ? background(bg) : "")
  end

  # returns the code for the given background color(s)
  def ANSIColor.background bgcolor
    make_code("on_" + bgcolor)
  end

  # returns the code for the given foreground color(s)
  def ANSIColor.foreground fgcolor
    make_code(fgcolor)
  end

  protected

  def ANSIColor.make_code str
    if str
      str.split.collect do |s|
        if attr = ATTRIBUTES[s]
          "\e[#{attr}m"
        else
          $stderr.puts "WARNING: ANSIColor::make_code(" + str + "): unknown color: " + s
          return ""
        end
      end.join("")
    else
      ""
    end
  end
end
