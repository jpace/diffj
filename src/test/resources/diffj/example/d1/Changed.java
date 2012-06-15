package org.incava.diffj.example;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Changed implements List {
    /**
     * The maximum size of this object.
     */
    public final static int MAX_SIZE = 317;

    private final int size;
    private int index;

    public Changed(int size) {
        this.size = size;
    }

    public void newMethod() {
    }
}
