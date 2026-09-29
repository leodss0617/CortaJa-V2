package com.serhat.autosub.cortaja.source;

public final class YtDlpMetadata {
    public final String id, extractor, title, channel, thumbnail, mediaUrl, audioUrl, manifestUrl;
    public final long durationMs;
    public final boolean live;

    public YtDlpMetadata(String id, String extractor, String title, String channel,
                         String thumbnail, long durationMs, boolean live,
                         String mediaUrl, String audioUrl, String manifestUrl) {
        this.id = id == null ? "" : id;
        this.extractor = extractor == null ? "" : extractor;
        this.title = title == null || title.isEmpty() ? "Vídeo público" : title;
        this.channel = channel == null ? "" : channel;
        this.thumbnail = thumbnail == null ? "" : thumbnail;
        this.durationMs = Math.max(0, durationMs);
        this.live = live;
        this.mediaUrl = mediaUrl == null ? "" : mediaUrl;
        this.audioUrl = audioUrl == null || audioUrl.isEmpty() ? this.mediaUrl : audioUrl;
        this.manifestUrl = manifestUrl == null ? "" : manifestUrl;
    }
}
