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

Java: DiffJ understands Java syntax (through version 1.6, at this writing), and
compares code based on the Java content.

Ignoring Whitespace: DiffJ omits whitespace when comparing code, so it works
well when, for example, tabs have been replaced with spaces, when indentation
levels have changed, or when the end-of-line style has changed.

Ignoring Order of Declarations: DiffJ does not consider the organization of the
code, such as the order of methods within a class. Thus if methods are reordered
(perhaps by accessibility), DiffJ will not report that as being a difference,
since the code itself has not changed. The order of declarations applies to all
Java type declarations (inner classes, fields, methods, and constructors).

Ignoring Comments: DiffJ skips comments when comparing code.

Showing Code Differences Narrowly: DiffJ reports the actual location (lines and
columns) of code changes, and the output (in context and highlight mode)
displays the exact change. Thus if a variable changes within a line of code,
that variable itself is highlighted for the from- and to-files.
