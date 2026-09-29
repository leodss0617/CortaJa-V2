package com.serhat.autosub.cortaja.source;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** yt-dlp-first resolver. Native providers are intentionally outside this class as fallback. */
public final class YtDlpSourceResolver {
    public interface Callback { void onResolved(SourceResolution resolution, String ytDlpVersion); void onError(String message); }
    private final YtDlpClient client;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private boolean updateAttempted;

    public YtDlpSourceResolver(YtDlpClient client) { this.client = client; }

    public SourceResolution resolve(String url) {
        PublicVideoSourceResolver.SourceRef ref = PublicVideoSourceResolver.parse(url);
        Exception first;
        try { return map(ref, client.getInfo(url)); }
        catch (Exception error) { first = error; }
        synchronized (this) {
            if (updateAttempted) throw userError(first);
            updateAttempted = true;
        }
        try {
            try { client.updateStable(); }
            catch (Exception stableError) { client.updateNightly(); }
            return map(ref, client.getInfo(url));
        } catch (Exception retryError) {
            throw userError(retryError);
        }
    }

    public void resolveAsync(String url, Callback callback) {
        executor.execute(() -> {
            try { callback.onResolved(resolve(url), client.version()); }
            catch (Exception e) { callback.onError(e.getMessage() == null ? "Não foi possível resolver este link público." : e.getMessage()); }
        });
    }

    private SourceResolution map(PublicVideoSourceResolver.SourceRef ref, YtDlpMetadata info) {
        PublicVideoSourceResolver.Provider provider = ref.provider;
        return SourceResolution.builder(provider, info.id.isEmpty() ? ref.sourceId : info.id)
                .sourceType(info.live ? "LIVE" : "VOD")
                .title(info.title).channel(info.channel).thumbnail(info.thumbnail).sourceUrl(ref.originalUrl)
                .durationMs(info.durationMs).isLive(info.live)
                .mediaUrl(info.mediaUrl).audioUrl(info.audioUrl).build();
    }

    private static RuntimeException userError(Exception error) {
        String raw = error.getMessage() == null ? "" : error.getMessage().toLowerCase(Locale.US);
        if (raw.contains("login") || raw.contains("private") || raw.contains("members") || raw.contains("drm"))
            return new PublicVideoSourceResolver.SourceResolutionException("Este conteúdo é privado ou exige autenticação. Use um link público.", error);
        return new PublicVideoSourceResolver.SourceResolutionException("Não foi possível obter a mídia pública deste link. Tente outro vídeo público.", error);
    }
}
