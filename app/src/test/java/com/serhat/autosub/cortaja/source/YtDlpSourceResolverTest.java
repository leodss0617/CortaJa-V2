package com.serhat.autosub.cortaja.source;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class YtDlpSourceResolverTest {
    @Test public void mapsYoutubeMetadataAndAudioUrl() {
        YtDlpSourceResolver resolver = new YtDlpSourceResolver(new FakeClient(false, false));
        SourceResolution result = resolver.resolve("https://youtu.be/abc123");
        assertEquals(PublicVideoSourceResolver.Provider.YOUTUBE, result.provider);
        assertEquals("Título público", result.title);
        assertEquals("https://audio", result.audioUrl);
        assertEquals(7_200_000L, result.durationMs);
    }

    @Test public void updatesOnceAndRetriesExtractorError() {
        FakeClient client = new FakeClient(true, false);
        SourceResolution result = new YtDlpSourceResolver(client).resolve("https://www.twitch.tv/videos/123");
        assertEquals(1, client.updates);
        assertEquals(PublicVideoSourceResolver.Provider.TWITCH, result.provider);
    }

    @Test public void rejectsUnsupportedUrlBeforeCallingYtDlp() {
        try {
            new YtDlpSourceResolver(new FakeClient(false, false)).resolve("https://example.com/video");
        } catch (PublicVideoSourceResolver.SourceResolutionException expected) {
            assertTrue(expected.getMessage().contains("não suportada"));
            return;
        }
        throw new AssertionError("URL inválida deveria falhar");
    }

    private static final class FakeClient implements YtDlpClient {
        boolean failFirst; boolean failNightly; int calls; int updates;
        FakeClient(boolean failFirst, boolean failNightly) { this.failFirst = failFirst; this.failNightly = failNightly; }
        public YtDlpMetadata getInfo(String url) {
            if (failFirst && calls++ == 0) throw new RuntimeException("extractor error");
            return new YtDlpMetadata("id", "youtube", "Título público", "Canal", "thumb",
                    7_200_000, false, "https://video", "https://audio", "https://manifest");
        }
        public void updateStable() { updates++; if (failNightly) throw new RuntimeException("stable"); }
        public void updateNightly() { updates++; }
        public String version() { return "test"; }
    }
}
