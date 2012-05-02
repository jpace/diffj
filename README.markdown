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
differences by reporting the type of Java code that changed.

FEATURES
--------

Java Content - DiffJ understands Java syntax (through version 1.6, at this
writing), and compares code based on the Java content.

For example, the following are considered equivalent:

#### src/test/resources/diffj/type/method/body/codechange/statement/*/Unchanged.java

<pre><code><span style="color: black; background: white">
    Integer[] ary  =  new Integer[ index( str, ch ) + str.length( ) ];
    
    Integer[] ary = new Integer[index(str, ch) + str.length()];
</span></code></pre>

Ignoring Whitespace: DiffJ omits whitespace when comparing code, so it works
well when, for example, tabs have been replaced with spaces, when indentation
levels have changed, or when the end-of-line style has changed.

Ignoring Order of Declarations: DiffJ does not consider the organization of the
code, such as the order of methods within a class. Thus if methods are reordered
(perhaps by accessibility), DiffJ will not report that as being a difference,
since the code itself has not changed. The order of declarations applies to all
Java type declarations (inner classes, fields, methods, and constructors).

Ignoring Comments: DiffJ skips comments when comparing code. Thus comments that
has been updated (perhaps as a result of running DoctorJ) will not be seen as a
change. For example, these are equivalent:

Showing Code Differences Narrowly: DiffJ reports the actual location (lines and
columns) of code changes, and the output (in context and highlight mode)
displays the exact change. Thus if a variable changes within a line of code,
that variable itself is highlighted for the from- and to-files.

<pre><code><span style="color: black; background: white">
2c2 parameter name changed from idx to index
  class Changed {
!     void changed(int <span style="color: red; background: black">idx</span>) {
      }
  }

  class Changed {
!     void changed(int <span style="color: yellow; background: black">index</span>) {
      }
  }
</span></code></pre>
