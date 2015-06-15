package org.incava.diffj.util;

public class DiffPoint {
    private final Integer startPoint;
    private final Integer endPoint;
    
    public DiffPoint(Integer startPoint, Integer endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }
    
    public Integer getStart() {
        return startPoint;
    }

    public Integer getEnd() {
        return endPoint;
    }
}
