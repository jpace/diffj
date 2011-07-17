package org.incava.jagol;

import java.io.*;
import java.util.*;

public class InvalidTypeException extends OptionException {

    private static final long serialVersionUID = 1L;    

    public InvalidTypeException(String msg) {
        super(msg);
    }

}
