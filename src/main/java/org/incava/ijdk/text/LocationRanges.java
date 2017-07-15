package org.incava.ijdk.text;

import org.incava.ijdk.lang.Pair;

/**
 * A pair of location ranges
 */
public class LocationRanges extends Pair<LocationRange, LocationRange> {
    public LocationRanges(LocationRange from, LocationRange to) {
        super(from, to);
    }

    public LocationRange from() {
        return first();
    }

    public LocationRange to() {
        return second();
    }
}
