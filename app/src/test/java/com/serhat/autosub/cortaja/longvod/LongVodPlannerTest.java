package com.serhat.autosub.cortaja.longvod;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LongVodPlannerTest {
    @Test public void createsExpectedBlocksForSupportedDurations() {
        LongVodPlanner planner = new LongVodPlanner(15);
        assertEquals(1, planner.plan(5 * 60_000L).size());
        assertEquals(2, planner.plan(17 * 60_000L).size());
        assertEquals(2, planner.plan(30 * 60_000L).size());
        assertEquals(4, planner.plan(60 * 60_000L).size());
        assertEquals(12, planner.plan(3 * 60 * 60_000L).size());
        assertEquals(24, planner.plan(6 * 60 * 60_000L).size());
        assertEquals(40, planner.plan(10 * 60 * 60_000L).size());
    }

    @Test public void clampsBlockSizeToTenTwentyMinutes() {
        assertEquals(6, new LongVodPlanner(1).plan(60 * 60_000L).size());
        assertEquals(3, new LongVodPlanner(60).plan(60 * 60_000L).size());
    }

    @Test public void resumesAfterLastPersistedBlock() {
        LongVodPlanner planner = new LongVodPlanner(15);
        List<LongVodBlock> blocks = planner.plan(60 * 60_000L);
        LongVodCheckpoint checkpoint = new LongVodCheckpoint("p", "source", 1,
                blocks.get(1).startMs, blocks.get(1).endMs, true);
        assertEquals(2, planner.nextBlockIndex(blocks, checkpoint));
    }

    @Test public void deduplicatesCandidatesAcrossBlockBoundaryAndRanksGlobally() {
        List<LongVodCandidate> candidates = Arrays.asList(
                new LongVodCandidate("a", 590_000, 1_090_000, 7.0),
                new LongVodCandidate("a-duplicate", 1_000_000, 1_500_000, 9.0),
                new LongVodCandidate("b", 3_000_000, 3_500_000, 8.0));
        List<LongVodCandidate> result = LongVodCandidateRanking.global(candidates, 2);
        assertEquals(2, result.size());
        assertEquals("a-duplicate", result.get(0).id);
        assertTrue(result.get(0).score >= result.get(1).score);
    }
}
