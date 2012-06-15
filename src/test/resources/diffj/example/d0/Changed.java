package org.incava.diffj.example;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
// import java.text.MessageFormat;
import java.io.File;

abstract public class Changed 
    implements Map
 {

  public void obsoleteMethod() {
  }

  public Changed( int  s ) 
   {
     size = s;
   }

  private int size;

  // @todo - comment this field
  static final public int MAX_SIZE = 317;

  private int idx;
    
}
