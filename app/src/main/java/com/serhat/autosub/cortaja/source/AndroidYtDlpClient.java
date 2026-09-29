package com.serhat.autosub.cortaja.source;

import android.content.Context;

import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import com.yausername.youtubedl_android.mapper.VideoInfo;

/** Thin adapter around the official yt-dlp Android runtime. */
public final class AndroidYtDlpClient implements YtDlpClient {
    private final Context context;

    public AndroidYtDlpClient(Context context) { this.context = context.getApplicationContext(); }

    public static void initialize(Context context) throws YoutubeDLException {
        YoutubeDL.getInstance().init(context.getApplicationContext());
        FFmpeg.getInstance().init(context.getApplicationContext());
    }

    @Override public YtDlpMetadata getInfo(String url) throws Exception {
        VideoInfo media = YoutubeDL.getInstance().getInfo(new YoutubeDLRequest(url).addOption("-f", "best"));
        VideoInfo audio = YoutubeDL.getInstance().getInfo(new YoutubeDLRequest(url).addOption("-f", "bestaudio/best"));
        long durationMs = Math.max(0, media.getDuration()) * 1000L;
        String extractor = first(media.getExtractorKey(), media.getExtractor());
        boolean live = durationMs == 0 || containsLive(media.getWebpageUrl());
        return new YtDlpMetadata(first(media.getId(), url), extractor, media.getTitle(), media.getUploader(),
                media.getThumbnail(), durationMs, live, media.getUrl(), audio.getUrl(), media.getManifestUrl());
    }

    @Override public void updateStable() throws Exception {
        YoutubeDL.getInstance().updateYoutubeDL(context, YoutubeDL.UpdateChannel._STABLE);
    }

    @Override public void updateNightly() throws Exception {
        YoutubeDL.getInstance().updateYoutubeDL(context, YoutubeDL.UpdateChannel._NIGHTLY);
    }

    @Override public String version() {
        String value = YoutubeDL.getInstance().versionName(context);
        return value == null ? "desconhecida" : value;
    }

    private static String first(String a, String b) { return a == null || a.isEmpty() ? (b == null ? "" : b) : a; }
    private static boolean containsLive(String url) { return url != null && url.toLowerCase().contains("/live"); }
}
