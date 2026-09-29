package com.serhat.autosub.cortaja;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CortaJaDomainTest {
    @Test public void validatesPublicYoutubeUrls() {
        assertTrue(CortaJaDomain.isValidYoutubeUrl("https://www.youtube.com/watch?v=abc123"));
        assertTrue(CortaJaDomain.isValidYoutubeUrl("https://youtu.be/abc123"));
        assertFalse(CortaJaDomain.isValidYoutubeUrl("arquivo local"));
        assertFalse(CortaJaDomain.isValidYoutubeUrl("https://example.com/video"));
    }

    @Test public void candidatePresentationUsesExactBoundsAndHumanScore() {
        CortaJaDomain.Candidate candidate = new CortaJaDomain.Candidate(
                1, "Momento engraçado", 91, 317_000L, 354_000L,
                "A conversa prepara a situação e entrega a reação completa.");
        assertEquals(37_000L, candidate.durationMs());
        assertEquals("9,1/10", candidate.scoreLabel());
        assertEquals(317_000L, candidate.startMs);
        assertEquals(354_000L, candidate.endMs);
    }

    @Test public void primaryNavigationIsCortajaPortuguese() {
        assertEquals("Início", CortaJaDomain.Navigation.INICIO.label);
        assertEquals("Projetos", CortaJaDomain.Navigation.PROJETOS.label);
        assertEquals("Meus Cortes", CortaJaDomain.Navigation.MEUS_CORTES.label);
        assertEquals("Configurações", CortaJaDomain.Navigation.CONFIGURACOES.label);
    }
}
