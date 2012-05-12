DiffJ
=====

NAME
----

diffj - compare Java files by content

SYNOPSIS
--------

diffj [ options ] from-file to-file

DESCRIPTION
-----------

Similar to diff(1), DiffJ compares files, specifically Java files, without
regard to formatting, organization, comments or whitespace, and outputs the
differences by reporting the type of Java code that changed. It is primarily for
comparing code when refactoring or fixing warnings, and also for reformatting,
ensuring that although whitespace and comments might have changed, the code did
not.

FEATURES
--------

Understanding Java Content: DiffJ handles Java syntax (through version 1.6, at
this writing), and compares code based on the actual Java code, not
line-by-line.

For example, the following are considered equivalent:

<pre><code>    Integer[] ary  =  new Integer[ index( str, ch ) + str.length( ) ];
    
    Integer[] ary = new Integer[index(str, ch) + str.length()];
</code></pre>

**Ignoring whitespace**. DiffJ ignores whitespace when comparing code, so it
works well when, for example, tabs have been replaced with spaces, when
indentation levels have changed, or when the end-of-line style has changed.

**Ignoring order of declarations**. DiffJ does not consider the organization of
the code, such as the order of methods within a class. Thus if methods are
reordered (perhaps by accessibility), DiffJ will not report that as being a
difference, since the code itself has not changed. The order of declarations
applies to all Java type declarations (inner classes, fields, methods, and
constructors).

**Ignoring order of import statements**. As with declarations, DiffJ does not
consider reordered import statements to be a change in the code.

**Ignoring comments**: DiffJ skips comments when comparing code. Thus comments
that have been updated (perhaps as a result of running DoctorJ) will not be seen
as a change.

**Showing added and deleted declarations**: DiffJ reports declarations that have
been added and deleted, described by their Java type. For example:

<pre><code>
d0/Removed.java <=> d1/Removed.java
2,3d1,4 method removed: contender(Double[], StringBuilder)
  class Removed {
!     public <span class="s0">void contender(Double[] dary, StringBuilder sb) {</span>
!     <span class="p">}</span>
  
      public void contender() {
      }
</code></pre>

**Showing parameters**: DiffJ reports constructor and methods parameters that have
been added, deleted, and changed:

<div width="100%" style="background: #111111; color: #EEEEEE; margin: 2em; padding: 0.25em 0.75em 0.75em 0.75em; ">
<pre><code>
2c2 parameter name changed from idx to index
  class Changed {
!     void changed(int <span style="color: red; background: black">idx</span>) {
      }
  }

  class Changed {
!     void changed(int <span style="color: yellow; background: black">index</span>) {
      }
  }
</code></pre></div>

**Showing code differences narrowly**: DiffJ reports the actual location (lines
and columns) of code changes, and the output (in context and highlight mode)
displays the exact change. Thus if a variable changes within a line of code,
that variable itself is highlighted for the from- and to-files.

<div width="100%" style="background: #111111; color: #EEEEEE; margin: 2em; padding: 0.25em 0.75em 0.75em 0.75em; ">
<pre><code>
d0/Changed.java <=> d1/Changed.java
3c3 code changed in meth(String, char)
  class Changed {
      void meth(String str, char ch) {
!         Integer[ ]  ary  =  new Integer[ <span style="color: red">str.indexOf ( ch, 317 ) + str.length ( )</span> ];
      }
  }

  class Changed {
      void meth(String str, char ch) {
!         Integer[] ary = new Integer[<span style="color: yellow">10 + str.length() + ch</span>];
      }
  }
</code></pre></div>

<p>

    <a href="https://github.com/jeugenepace/diffj/raw/7a93ee2fbfe01ba6a0f2a8193d356cc4f8b53f23/src/site/resources/img/diffj-codechange-statement.png" target="_blank">
    <img src="https://github.com/jeugenepace/diffj/raw/7a93ee2fbfe01ba6a0f2a8193d356cc4f8b53f23/src/site/resources/img/diffj-codechange-statement.png" alt="codechange statement" style="max-width:100%;"/></a></p>
</p>

**Configuration files**: DiffJ looks for a configuration file as ~/.diffrc, and
uses the name/value pairs there for its options. All options are supported, with
the syntax "name: value". For boolean options (such as
`--highlight`/`--no-highlight`), the value should be either true or false.

As an example, the following file sets DiffJ to use the source version as 1.6,
and to show context with non-default colors:

    context: true
    highlight: true
    from-color: bold blue on green
    to-color: underscore magenta on cyan


OPTIONS
-------

    --brief   
        Display output in brief form.

    --context
        Show context (non-brief form only).

    --[no-]highlight
        Use colors (context output only).

    --recurse
        Process directories recursively.

    --from-source VERSION
        The Java source version of from-file (default: 1.5).

    --to-source VERSION
        The Java source version of to-file (default: 1.5).

    --from-color COLOR
        The text color of the from-file text (default: red).

    --to-color COLOR
        The text color of the to-file text (default: yellow).

    --source VERSION
        The Java source version of from-file and to-file (default: 1.5).

    -u
        Output unified context. Unused; exists for compatibility with GNU diff.

    -L NAME  --name NAME
        Set the first/second name to be displayed. This is useful for diffing
        with an external program, such as svn, where the file names are the temp
        files, and which passes in the real names as arguments.

    --verbose
        Run in verbose mode (for debugging).

    -h  --help
        Show help summary.

    -v  --version
        Display the version.

EXAMPLES
--------

    % diffj old/Foo.java new/Foo.java
        Compares the two files, in the brief output format.

    % diffj --context branches/3.1.4 trunk
        Compares the files in the two directories, reporting their differences
        with context.

    % diffj --highlight -r branches/3.1.4 branches/3.1.5
        Compares the two directories recursively, reporting their differences
        with context, and changes highlighted.

    % diffj --highlight --from-color "bold red on white" --to-color "cyan on black" ../v1.2.3 .
        Displays differences in the given colors.

    % diffj --from-source 1.4 --to-source 1.6 -r ~myproj/old ~myproj/current
        Compares the code, using Java 1.4 and 1.6 as the source for the from-
        and the to-files.
