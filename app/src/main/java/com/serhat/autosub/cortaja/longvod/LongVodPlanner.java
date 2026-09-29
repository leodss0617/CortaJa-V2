package com.serhat.autosub.cortaja.longvod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Plans bounded audio windows; it never materializes media or PCM. */
public final class LongVodPlanner {
    public static final int MIN_BLOCK_MINUTES = 10;
    public static final int MAX_BLOCK_MINUTES = 20;
    private final long blockMs;

    public LongVodPlanner(int blockMinutes) {
        int safe = Math.max(MIN_BLOCK_MINUTES, Math.min(MAX_BLOCK_MINUTES, blockMinutes));
        blockMs = safe * 60_000L;
    }

    public List<LongVodBlock> plan(long durationMs) {
        if (durationMs <= 0) return Collections.emptyList();
        List<LongVodBlock> result = new ArrayList<>();
        long start = 0;
        int index = 0;
        while (start < durationMs) {
            long end = Math.min(durationMs, start + blockMs);
            result.add(new LongVodBlock(index++, start, end));
            start = end;
        }
        return result;
    }

    public int nextBlockIndex(List<LongVodBlock> blocks, LongVodCheckpoint checkpoint) {
        if (checkpoint == null || !checkpoint.transcriptPersisted) return 0;
        return Math.min(checkpoint.blockIndex + 1, blocks.size());
    }
}
