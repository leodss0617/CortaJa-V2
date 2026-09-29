package com.serhat.autosub.cortaja;

import com.serhat.autosub.shorts.ShortsCandidate;
import java.util.ArrayList;
import java.util.List;

/** Product façade for mapping the existing Shorts engine to CortaJá cards and media bounds. */
public final class CortaJaUseCases {
    private CortaJaUseCases() { }

    public static List<CortaJaDomain.Candidate> toCandidates(List<ShortsCandidate> source) {
        List<CortaJaDomain.Candidate> result = new ArrayList<>();
        if (source == null) return result;
        for (int i = 0; i < source.size(); i++) {
            ShortsCandidate c = source.get(i);
            result.add(new CortaJaDomain.Candidate(i + 1, c.getTitle(), c.getScore(), c.getStartMs(), c.getEndMs(),
                    c.getReason().isEmpty() ? "Momento completo, com começo, desenvolvimento e desfecho." : c.getReason()));
        }
        return result;
    }

    public static long[] exactPreviewBounds(CortaJaDomain.Candidate candidate) {
        if (candidate == null) return new long[]{0L, 0L};
        return new long[]{candidate.startMs, candidate.endMs};
    }
}
