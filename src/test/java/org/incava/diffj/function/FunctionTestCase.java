package org.incava.diffj.function;

import org.incava.analysis.FileDiff;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.LocationRanges;

import static org.incava.diffj.params.Parameters.PARAMETER_ADDED;
import static org.incava.diffj.params.Parameters.PARAMETER_NAME_CHANGED;
import static org.incava.diffj.params.Parameters.PARAMETER_REMOVED;
import static org.incava.diffj.params.Parameters.PARAMETER_REORDERED;
import static org.incava.diffj.params.Parameters.PARAMETER_REORDERED_AND_RENAMED;
import static org.incava.diffj.params.Parameters.PARAMETER_TYPE_CHANGED;

public class FunctionTestCase extends ItemsTest {
    public FunctionTestCase(String name) {
        super(name);
    }

    public Lines lines(String ... lines) {
        return new Lines(lines);
    }

    public FileDiff paramReordered(String name, Integer oldIndex, Integer newIndex, LocationRange fromLoc, LocationRange toLoc) {
        return makeCodeChangedRef(PARAMETER_REORDERED, new String[] { name, oldIndex.toString(), newIndex.toString() }, fromLoc, toLoc);
    }

    public FileDiff paramAdded(String name, LocationRange fromLoc, LocationRange toLoc) {
        return makeCodeChangedRef(PARAMETER_ADDED, new String[] { name }, fromLoc, toLoc);
    }

    public FileDiff paramRemoved(String name, LocationRange fromLoc, LocationRange toLoc) {
        return makeCodeChangedRef(PARAMETER_REMOVED, new String[] { name }, fromLoc, toLoc);
    }

    public FileDiff paramTypeChanged(String oldType, String newType, LocationRange fromLoc, LocationRange toLoc) {
        return makeCodeChangedRef(PARAMETER_TYPE_CHANGED, new String[] { oldType, newType }, fromLoc, toLoc);
    }

    public FileDiff paramNameChanged(String oldName, String newName, LocationRange fromLoc, LocationRange toLoc) {
        return makeCodeChangedRef(PARAMETER_NAME_CHANGED, new String[] { oldName, newName }, fromLoc, toLoc);
    }

    public FileDiff paramReorderedAndRenamed(String oldName, Integer oldIndex, Integer newIndex, String newName, LocationRange fromLoc, LocationRange toLoc) {
        return makeCodeChangedRef(PARAMETER_REORDERED_AND_RENAMED, new String[] { oldName, oldIndex.toString(), newIndex.toString(), newName }, fromLoc, toLoc);
    }    
}
