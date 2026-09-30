package com.serhat.autosub.cortaja.source;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PublicMediaRoutingPolicyTest {
    @Test public void allKnownPublicVodDurationsUseYtDlp() {
        long[] durations = {5, 17, 30, 600};
        for (long minutes : durations) {
            SourceResolution r = SourceResolution.builder(PublicVideoSourceResolver.Provider.YOUTUBE, "id")
                    .sourceUrl("https://youtube.com/watch?v=id").mediaUrl("https://media").audioUrl("https://audio")
                    .durationMs(minutes * 60_000L).build();
            assertEquals(PublicMediaRoutingPolicy.Route.PUBLIC_YTDLP, PublicMediaRoutingPolicy.route(r));
        }
    }

    @Test public void localUrisRemainLocal() {
        SourceResolution content = SourceResolution.builder(PublicVideoSourceResolver.Provider.YOUTUBE, "local")
                .sourceUrl("content://corta/video").mediaUrl("content://corta/video").durationMs(300_000).build();
        SourceResolution file = SourceResolution.builder(PublicVideoSourceResolver.Provider.YOUTUBE, "local")
                .sourceUrl("file:///tmp/video.mp4").mediaUrl("file:///tmp/video.mp4").durationMs(300_000).build();
        assertEquals(PublicMediaRoutingPolicy.Route.LOCAL_FILE, PublicMediaRoutingPolicy.route(content));
        assertEquals(PublicMediaRoutingPolicy.Route.LOCAL_FILE, PublicMediaRoutingPolicy.route(file));
        assertTrue(PublicMediaRoutingPolicy.isRemote("https://media"));
        assertFalse(PublicMediaRoutingPolicy.isRemote("content://media"));
    }

    @Test public void liveRemoteIsExplicitlyUnavailable() {
        SourceResolution live = SourceResolution.builder(PublicVideoSourceResolver.Provider.TWITCH, "live")
                .sourceUrl("https://twitch.tv/channel").mediaUrl("https://media").isLive(true).build();
        assertEquals(PublicMediaRoutingPolicy.Route.LIVE_UNAVAILABLE, PublicMediaRoutingPolicy.route(live));
    }
}
