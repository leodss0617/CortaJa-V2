package com.serhat.autosub.shorts;

import com.serhat.autosub.subtitles.SubtitleGenerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LocalShortsFallbackAnalyzerTest {
    @Test public void selectsCompleteThoughtAndRanksHookPayoff() {
        List<SubtitleGenerator.SubtitleEntry> transcript = entries(
                "Today I tried the impossible challenge", "and everyone said I would fail.",
                "But the first step changed everything.", "The final result surprised the entire room!"
        );

        List<ShortsCandidate> result = new LocalShortsFallbackAnalyzer()
                .analyze(transcript, 1, 2, 20);

        assertEquals(1, result.size());
        ShortsCandidate candidate = result.get(0);
        assertEquals(1, candidate.getStartSubtitleId());
        assertEquals(4, candidate.getEndSubtitleId());
        assertTrue(candidate.getScore() >= 60);
    }

    @Test public void removesOverlappingSemanticallySimilarMoments() {
        List<SubtitleGenerator.SubtitleEntry> transcript = entries(
                "The hidden trick is simple", "the hidden trick changes the result!",
                "Later, the hidden trick works again", "A completely different recipe begins.",
                "First add water", "then add flour", "and serve it hot!"
        );

        List<ShortsCandidate> result = new LocalShortsFallbackAnalyzer()
                .analyze(transcript, 5, 2, 20);

        assertTrue(result.size() <= 2);
        assertEquals(1, result.get(0).getStartSubtitleId());
    }

    @Test public void neverCutsInsideSentenceWhenExpanding() {
        List<SubtitleGenerator.SubtitleEntry> transcript = entries(
                "Question: why did this happen?", "Because the setup was wrong.",
                "The answer is to change one small step.", "That is the payoff."
        );

        List<ShortsCandidate> result = new LocalShortsFallbackAnalyzer()
                .analyze(transcript, 1, 3, 20);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getStartSubtitleId());
        assertEquals(4, result.get(0).getEndSubtitleId());
    }

    private static List<SubtitleGenerator.SubtitleEntry> entries(String... text) {
        List<SubtitleGenerator.SubtitleEntry> result = new ArrayList<>();
        for (int i = 0; i < text.length; i++) {
            result.add(new SubtitleGenerator.SubtitleEntry(i + 1,
                    String.format("00:00:%02d,000", i * 2),
                    String.format("00:00:%02d,000", i * 2 + 2), text[i]));
        }
        return result;
    }
}
