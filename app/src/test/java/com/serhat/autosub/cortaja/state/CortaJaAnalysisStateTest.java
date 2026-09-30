package com.serhat.autosub.cortaja.state;

import com.serhat.autosub.shorts.ShortsProject;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CortaJaAnalysisStateTest {
    @Test public void falseAnalyzingAloneIsNotSuccess() {
        assertFalse(CortaJaAnalysisState.isSuccess(null, ""));
    }

    @Test public void successRequiresProjectWithCandidates() {
        ShortsProject project = new ShortsProject(1, "", 3, 15, 60);
        assertFalse(CortaJaAnalysisState.isSuccess(project, ""));
        assertFalse(CortaJaAnalysisState.isSuccess(project, "erro real"));
    }

    @Test public void pendingRequestPreservesAllInputs() {
        LongVodPendingRequest request = new LongVodPendingRequest("p", "https://youtu.be/a", 36_000, "2026");
        assertTrue(request.projectId.equals("p") && request.sourceUrl.contains("youtu")
                && request.durationMs == 36_000 && request.resolverVersion.equals("2026"));
    }
}
