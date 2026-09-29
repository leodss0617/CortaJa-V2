package com.serhat.autosub.cortaja.longvod;

public final class LongVodCandidate {
    public final String id;
    public final long startMs;
    public final long endMs;
    public final double score;

    public LongVodCandidate(String id, long startMs, long endMs, double score) {
        this.id = id;
        this.startMs = startMs;
        this.endMs = endMs;
        this.score = score;
    }
}
