package com.serhat.autosub.cortaja.state;

public final class LongVodPendingRequest {
    public final String projectId;
    public final String sourceUrl;
    public final long durationMs;
    public final String resolverVersion;

    public LongVodPendingRequest(String projectId, String sourceUrl, long durationMs, String resolverVersion) {
        this.projectId = projectId;
        this.sourceUrl = sourceUrl;
        this.durationMs = durationMs;
        this.resolverVersion = resolverVersion;
    }
}
