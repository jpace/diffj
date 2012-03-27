package org.incava.java;

public enum JavaVersionTypes {
    Version_1_3("1.3"),
    Version_1_4("1.4"),
    Version_1_5("1.5"),
    Version_1_6("1.6");

    private final String version;
    
    JavaVersionTypes(String ver) {
        version = ver;
    }

    public String toString() {
        return version;
    }
}
