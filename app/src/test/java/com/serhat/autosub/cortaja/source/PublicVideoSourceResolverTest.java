package com.serhat.autosub.cortaja.source;

import org.junit.Test;
import static org.junit.Assert.*;

public class PublicVideoSourceResolverTest {
    @Test public void resolvesYoutubeWatchAndShortUrls() {
        assertEquals(PublicVideoSourceResolver.Provider.YOUTUBE,
                PublicVideoSourceResolver.providerFor("https://www.youtube.com/watch?v=abc123"));
        assertEquals(PublicVideoSourceResolver.Provider.YOUTUBE,
                PublicVideoSourceResolver.providerFor("https://youtu.be/abc123"));
    }
    @Test public void resolvesYoutubeLive() {
        PublicVideoSourceResolver.SourceRef ref = PublicVideoSourceResolver.parse("https://www.youtube.com/live/abc123");
        assertEquals(PublicVideoSourceResolver.Provider.YOUTUBE, ref.provider);
        assertEquals("abc123", ref.sourceId);
        assertTrue(ref.liveHint);
    }
    @Test public void resolvesTwitchVodAndLive() {
        PublicVideoSourceResolver.SourceRef vod = PublicVideoSourceResolver.parse("https://www.twitch.tv/videos/987654321");
        PublicVideoSourceResolver.SourceRef live = PublicVideoSourceResolver.parse("https://www.twitch.tv/creator_name");
        assertEquals(PublicVideoSourceResolver.Provider.TWITCH, vod.provider);
        assertEquals("987654321", vod.sourceId);
        assertFalse(vod.liveHint);
        assertEquals("creator_name", live.sourceId);
        assertTrue(live.liveHint);
    }
    @Test public void resolvesKickVodAndLive() {
        PublicVideoSourceResolver.SourceRef vod = PublicVideoSourceResolver.parse("https://kick.com/creator/videos/123456");
        PublicVideoSourceResolver.SourceRef live = PublicVideoSourceResolver.parse("https://kick.com/creator");
        assertEquals(PublicVideoSourceResolver.Provider.KICK, vod.provider);
        assertEquals("123456", vod.sourceId);
        assertEquals("creator", live.sourceId);
        assertTrue(live.liveHint);
    }
    @Test public void rejectsUnknownUrl() {
        try { PublicVideoSourceResolver.parse("https://example.com/video"); fail("expected invalid URL"); }
        catch (PublicVideoSourceResolver.SourceResolutionException expected) { }
    }
    @Test public void mediaInfoIsTheCommonPipelineBoundary() {
        SourceResolution resolution = SourceResolution.builder(PublicVideoSourceResolver.Provider.TWITCH, "1")
                .title("VOD público").mediaUrl("https://usher.ttvnw.net/vod/1.m3u8").isLive(false).build();
        assertEquals(resolution.mediaUrl, resolution.audioUrl);
        assertTrue(resolution.hasUsableMedia());
    }
}
