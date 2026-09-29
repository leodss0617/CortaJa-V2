package com.serhat.autosub.cortaja.source;

import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;

/** Downloads only one bounded section. Analysis callers should pass audio format. */
public final class YtDlpSectionDownloader {
    public File downloadAudioSection(String url, long startMs, long endMs, File output) throws Exception {
        String section = "*" + seconds(startMs) + "-" + seconds(endMs);
        YoutubeDLRequest request = new YoutubeDLRequest(url)
                .addOption("--no-playlist")
                .addOption("--download-sections", section)
                .addOption("--force-keyframes-at-cuts")
                .addOption("-f", "bestaudio/best")
                .addOption("-x")
                .addOption("--audio-format", "wav")
                .addOption("--no-part")
                .addOption("-o", output.getAbsolutePath());
        YoutubeDL.getInstance().execute(request);
        if (!output.isFile() || output.length() == 0) throw new IllegalStateException("yt-dlp não gerou o áudio do bloco");
        return output;
    }

    public File downloadVideoSection(String url, long startMs, long endMs, File output) throws Exception {
        String section = "*" + seconds(startMs) + "-" + seconds(endMs);
        YoutubeDLRequest request = new YoutubeDLRequest(url)
                .addOption("--no-playlist")
                .addOption("--download-sections", section)
                .addOption("--force-keyframes-at-cuts")
                .addOption("-f", "best")
                .addOption("--no-part")
                .addOption("-o", output.getAbsolutePath());
        YoutubeDL.getInstance().execute(request);
        if (!output.isFile() || output.length() == 0) throw new IllegalStateException("yt-dlp não gerou o trecho de vídeo");
        return output;
    }

    private static String seconds(long ms) { return String.format(java.util.Locale.US, "%.3f", ms / 1000d); }
}
