package com.serhat.autosub.cortaja.longvod;

public final class LongVodCheckpoint {
    public final String projectId;
    public final String sourceId;
    public final int blockIndex;
    public final long startMs;
    public final long endMs;
    public final boolean transcriptPersisted;

    public LongVodCheckpoint(String projectId, String sourceId, int blockIndex,
                             long startMs, long endMs, boolean transcriptPersisted) {
        this.projectId = projectId;
        this.sourceId = sourceId;
        this.blockIndex = blockIndex;
        this.startMs = startMs;
        this.endMs = endMs;
        this.transcriptPersisted = transcriptPersisted;
    }
}
