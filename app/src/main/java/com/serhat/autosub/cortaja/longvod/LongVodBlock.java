package com.serhat.autosub.cortaja.longvod;

public final class LongVodBlock {
    public final int index;
    public final long startMs;
    public final long endMs;

    public LongVodBlock(int index, long startMs, long endMs) {
        this.index = index;
        this.startMs = startMs;
        this.endMs = endMs;
    }
}
