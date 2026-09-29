package com.serhat.autosub.cortaja;

import com.serhat.autosub.shorts.ShortsCandidate;
import org.junit.Test;
import java.util.Collections;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CortaJaUseCasesTest {
    @Test public void mapsEngineCandidateToPortugueseProductCard() {
        ShortsCandidate engine = new ShortsCandidate(1, 2, 317000, 354000, "Momento engraçado", "", "Entrega a reação completa.", 91);
        CortaJaDomain.Candidate card = CortaJaUseCases.toCandidates(Collections.singletonList(engine)).get(0);
        assertEquals(1, card.ranking); assertEquals("Momento engraçado", card.category); assertEquals("9,1/10", card.scoreLabel());
        assertEquals("Entrega a reação completa.", card.reason);
    }

    @Test public void previewKeepsExactEngineBounds() {
        CortaJaDomain.Candidate card = new CortaJaDomain.Candidate(1, "Humor", 91, 317000, 354000, "ok");
        assertArrayEquals(new long[]{317000, 354000}, CortaJaUseCases.exactPreviewBounds(card));
    }
}
