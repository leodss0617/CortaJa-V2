package com.serhat.autosub.cortaja.longvod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class LongVodCandidateRanking {
    private LongVodCandidateRanking() {}

    public static List<LongVodCandidate> global(List<LongVodCandidate> input, int limit) {
        List<LongVodCandidate> sorted = new ArrayList<>();
        for (LongVodCandidate candidate : input) {
            boolean duplicate = false;
            for (LongVodCandidate kept : sorted) {
                if (overlaps(candidate, kept)) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) sorted.add(candidate);
            else {
                for (int i = 0; i < sorted.size(); i++) {
                    if (overlaps(candidate, sorted.get(i)) && candidate.score > sorted.get(i).score) {
                        sorted.set(i, candidate);
                        break;
                    }
                }
            }
        }
        sorted.sort(Comparator.comparingDouble((LongVodCandidate c) -> c.score).reversed());
        if (limit < sorted.size()) return new ArrayList<>(sorted.subList(0, Math.max(0, limit)));
        return sorted;
    }

    private static boolean overlaps(LongVodCandidate a, LongVodCandidate b) {
        long overlap = Math.min(a.endMs, b.endMs) - Math.max(a.startMs, b.startMs);
        long shorter = Math.min(a.endMs - a.startMs, b.endMs - b.startMs);
        return overlap > 0 && shorter > 0 && overlap * 2 >= shorter;
    }
}
