package com.serhat.autosub.cortaja.source;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** yt-dlp-first resolver with bounded client strategies and one update cycle. */
public final class YtDlpSourceResolver {
    public interface Callback { void onResolved(SourceResolution resolution, String ytDlpVersion); void onError(String message); }
    private final YtDlpClient client;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private boolean updateAttempted;
    public YtDlpSourceResolver(YtDlpClient client) { this.client = client; }

    public SourceResolution resolve(String url) {
        PublicVideoSourceResolver.SourceRef ref = PublicVideoSourceResolver.parse(url);
        Exception last = null;
        String[] clients = {"", "android_vr", "web_safari"};
        for (String playerClient : clients) {
            try { return map(ref, client.getInfo(url, playerClient)); }
            catch (Exception error) { last = error; if (!isRetryable(error)) break; }
        }
        synchronized (this) {
            if (updateAttempted) throw userError(last);
            updateAttempted = true;
        }
        try {
            try { client.updateStable(); } catch (Exception stableError) { client.updateNightly(); }
            return map(ref, client.getInfo(url, ""));
        } catch (Exception retryError) { throw userError(retryError); }
    }

    public void resolveAsync(String url, Callback callback) {
        executor.execute(() -> { try { callback.onResolved(resolve(url), client.version()); }
            catch (Exception e) { callback.onError(e.getMessage() == null ? "Não foi possível resolver este link público." : e.getMessage()); } });
    }

    private SourceResolution map(PublicVideoSourceResolver.SourceRef ref, YtDlpMetadata info) {
        return SourceResolution.builder(ref.provider, info.id.isEmpty() ? ref.sourceId : info.id)
                .sourceType(info.live ? "LIVE" : "VOD").title(info.title).channel(info.channel).thumbnail(info.thumbnail)
                .sourceUrl(ref.originalUrl).durationMs(info.durationMs).isLive(info.live)
                .mediaUrl(info.mediaUrl).audioUrl(info.audioUrl).build();
    }
    private static boolean isRetryable(Exception error) {
        String raw = error.getMessage() == null ? "" : error.getMessage().toLowerCase(Locale.US);
        return raw.contains("403") || raw.contains("forbidden") || raw.contains("sabr") || raw.contains("signature")
                || raw.contains("challenge") || raw.contains("extractor") || raw.contains("requested format");
    }
    private static RuntimeException userError(Exception error) {
        String raw = error == null || error.getMessage() == null ? "" : error.getMessage().toLowerCase(Locale.US);
        if (raw.contains("login") || raw.contains("private") || raw.contains("members") || raw.contains("drm"))
            return new PublicVideoSourceResolver.SourceResolutionException("Este conteúdo é privado ou exige autenticação. Use um link público.", error);
        return new PublicVideoSourceResolver.SourceResolutionException("Não foi possível obter a mídia pública deste link após atualizar o resolvedor.", error);
    }
}
