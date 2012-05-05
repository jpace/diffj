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

<div>this is a test to see whether github strips divs</div>

FEATURES
--------

Understanding Java Content: DiffJ handles Java syntax (through version 1.6, at
this writing), and compares code based on the actual Java code, not
line-by-line.

For example, the following are considered equivalent:

<div width="100%" style="background: #111111; color: #EEEEEE; margin: 2em; padding: 0.25em 0.75em 0.75em 0.75em; ">
<pre><code>
Integer[] ary  =  new Integer[ index( str, ch ) + str.length( ) ];

Integer[] ary = new Integer[index(str, ch) + str.length()];
</code></pre></div>

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
that has been updated (perhaps as a result of running DoctorJ) will not be seen
as a change.

**Showing added and deleted declarations**: DiffJ reports declarations that have
been added and deleted, described by their Java type. For example:

<div width="100%" style="background: #111111; color: #EEEEEE; margin: 2em; padding: 0.25em 0.75em 0.75em 0.75em; ">
<pre><code>
d0/Removed.java <=> d1/Removed.java
2,3d1,4 method removed: contender(Double[], StringBuilder)
  class Removed {
!     public <span style="color: red; background: black">void contender(Double[] dary, StringBuilder sb) {</span>
!     <span style="color: red; background: black">}</span>
  
      public void contender() {
      }
</code></pre></div>

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
