#!/usr/bin/ruby -w
# -*- ruby -*-

# Negates the given expression.
class NegatedRegexp < Regexp

  def match(str)
    !super
  end

end

class Regexp

  # shell expressions to Ruby regular expression sequences
  SH2RE = Hash[
    '*'  => '.*', 
    '?'  => '.',
    # '['  => '\[',
    # ']'  => '\]',
    '.'  => '\.',
    '$'  => '\$',
    '/'  => '\/',
    '('  => '\(',
    ')'  => '\)',
  ]

  # Returns a regular expression for the given Unix file system expression.
  
  def self.unixre_to_string(pat)
    pat.gsub(%r{(\\.)|(.)}) do
      $1 || SH2RE[$2] || $2
    end
  end

  WORD_START_RE = Regexp.new('^                 # start of word
                                [\[\(]*         # parentheses or captures, maybe
                                (?: \\\w | \\w) # literal \w, or what \w matches
                              ',
                             Regexp::EXTENDED)
  
  WORD_END_RE = Regexp.new('(?:                 # one of the following:
                                \\\w            #   - \w for regexp
                              |                 # 
                                \w              #   - a literal A-Z, a-z, 0-9, or _
                              |                 # 
                                (?:             #   - one of the following:
                                    \[[^\]]*    #         LB, with no RB until:
                                    (?:         #      - either of:
                                        \\w     #         - "\w"
                                      |         # 
                                        \w      #         - a literal A-Z, a-z, 0-9, or _
                                    )           #      
                                    [^\]]*\]    #      - anything (except RB) to the next RB
                                )               #
                            )                   #
                            (?:                 # optionally, one of the following:
                                \*              #   - "*"
                              |                 # 
                                \+              #   - "+"
                              |                 #
                                \?              #   - "?"
                              |                 #
                                \{\d*,\d*\}   #   - "{3,4}", "{,4}, "{,123}" (also matches the invalid {,})
                            )?                  #
                            $                   # fin
                           ', 
                           Regexp::EXTENDED)

  # Handles negation, whole words, and ignore case (Ruby no longer supports
  # Rexexp.new(/foo/i), as of 1.8).
  
  def self.create(pat, args = Hash.new)
    negated    = args[:negated]
    ignorecase = args[:ignorecase]
    wholewords = args[:wholewords]
    wholelines = args[:wholelines]
    extended   = args[:extended]
    multiline  = args[:multiline]

    pattern    = pat.dup
    
    # we handle a ridiculous number of possibilities here:
    #     /foobar/     -- "foobar"
    #     /foo/bar/    -- "foo", then slash, then "bar"
    #     /foo\/bar/   -- same as above
    #     /foo/bar/i   -- same as above, case insensitive
    #     /foo/bari    -- "/foo/bari" exactly
    #     /foo/bar\/i  -- "/foo/bar/i" exactly
    #     foo/bar/     -- "foo/bar/" exactly
    #     foo/bar/     -- "foo/bar/" exactly

    if pattern.sub!(%r{ ^ !(?=/) }x, "")
      negated = true
    end

    if pattern.sub!(%r{ ^ \/ (.*[^\\]) \/ ([mix]+) $ }x) { $1 }
      modifiers  = $2
      
      multiline  ||= modifiers.index('m')
      ignorecase ||= modifiers.index('i')
      extended   ||= modifiers.index('x')
    else
      pattern.sub!(%r{ ^\/ (.*[^\\]) \/ $ }x) { $1 }
    end
    
    if wholewords
      # sanity check:

      errs = [
        [ WORD_START_RE, "start" ],
        [ WORD_END_RE,   "end"   ]
      ].collect do |ary|
        re, err = *ary
        re.match(pattern) ? nil : err
      end.compact
      
      if errs.length > 0
        Log.warn "pattern '#{pattern}' does not " + errs.join(" and ") + " on a word boundary"
      end
      pattern = '\b' + pattern + '\b'
    elsif wholelines
      pattern = '^'  + pattern + '$'        # ' for emacs
    end
    
    reclass = negated ? NegatedRegexp : Regexp

    flags = [
      [ ignorecase, Regexp::IGNORECASE ],
      [ extended,   Regexp::EXTENDED   ],
      [ multiline,  Regexp::MULTILINE  ]
    ].inject(0) do |tot, ary|
      val, flag = *ary
      tot | (val ? flag : 0)
    end
    
    reclass.new(pattern, flags)
  end

  def self.matches_word_start?(pat)
    WORD_START_RE.match(pat)
  end

  def self.matches_word_end?(pat)
    WORD_END_RE.match(pat)
  end

  # applies Perl-style substitution (s/foo/bar/).
  def self.perl_subst(pat)
  end
  
end
