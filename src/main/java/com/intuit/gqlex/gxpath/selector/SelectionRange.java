package com.intuit.gqlex.gxpath.selector;

public class SelectionRange {
    private int rangeStart=0;
    private int rangeEnd=Integer.MAX_VALUE;
    private boolean rangeEndAll;

    public int getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(int rangeStart) {
        this.rangeStart = rangeStart;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(int rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public void setRangeEndAll(boolean rangeEndAll) {
        this.rangeEndAll = rangeEndAll;
    }

    public boolean isRangeEndAll() {
        return rangeEndAll;
    }
}
