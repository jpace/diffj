package org.incava.bar;

import java.util.List;

import java.io.*;
import java.lang.reflect.*;

abstract public class Unchanged extends java.util.ArrayList
 {
    
  final static public int MAXIMUM_SIZE = 317;

  abstract double calculate(String str);

  double recalculate(int val, String str)
   { 
     while (val < str.length()) {
      val += calculate(str);
     }
     return val;
   }
 }
