package com.serhat.autosub.cortaja.source;

import java.util.Locale;

public final class SourceResolution {
    public final PublicVideoSourceResolver.Provider provider;
    public final String sourceId, sourceType, title, channel, thumbnail, mediaUrl, audioUrl, sourceUrl;
    public final long durationMs;
    public final boolean isLive;

    private SourceResolution(Builder b) {
        provider=b.provider; sourceId=b.sourceId; sourceType=b.sourceType; title=b.title;
        channel=b.channel; thumbnail=b.thumbnail; sourceUrl=b.sourceUrl; durationMs=Math.max(0,b.durationMs); isLive=b.isLive;
        mediaUrl=b.mediaUrl; audioUrl=b.audioUrl;
    }
    public boolean hasUsableMedia() { return mediaUrl != null && !mediaUrl.isEmpty(); }
    public static Builder builder(PublicVideoSourceResolver.Provider provider, String sourceId) { return new Builder(provider,sourceId); }
    public static final class Builder {
        private final PublicVideoSourceResolver.Provider provider; private final String sourceId;
        private String sourceType="VOD", title="Vídeo público", channel="", thumbnail="", sourceUrl="", mediaUrl="", audioUrl="";
        private long durationMs; private boolean isLive;
        private Builder(PublicVideoSourceResolver.Provider p,String id){provider=p;sourceId=id;}
        public Builder sourceType(String v){sourceType=v;return this;} public Builder title(String v){title=v;return this;}
        public Builder channel(String v){channel=v;return this;} public Builder thumbnail(String v){thumbnail=v;return this;} public Builder sourceUrl(String v){sourceUrl=v==null?"":v;return this;}
        public Builder durationMs(long v){durationMs=v;return this;} public Builder isLive(boolean v){isLive=v;sourceType=v?"LIVE":sourceType;return this;}
        public Builder mediaUrl(String v){mediaUrl=v==null?"":v;return this;} public Builder audioUrl(String v){audioUrl=v==null?mediaUrl:v;return this;}
        public SourceResolution build(){if(audioUrl.isEmpty())audioUrl=mediaUrl;return new SourceResolution(this);}
    }
}
