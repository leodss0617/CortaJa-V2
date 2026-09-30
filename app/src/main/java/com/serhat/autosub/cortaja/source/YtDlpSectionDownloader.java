package com.serhat.autosub.cortaja.source;

import android.content.Context;

import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;
import java.io.RandomAccessFile;

/** Downloads one bounded section and refreshes yt-dlp/format strategy on transient YouTube errors. */
public final class YtDlpSectionDownloader {
    private final Context context;
    public YtDlpSectionDownloader() { this.context = null; }
    public YtDlpSectionDownloader(Context context) { this.context = context == null ? null : context.getApplicationContext(); }

    public File downloadAudioSection(String url, long startMs, long endMs, File output) throws Exception {
        String section = "*" + seconds(startMs) + "-" + seconds(endMs);
        String[] clients = {"", "youtube:player_client=android_vr", "youtube:player_client=web_safari", ""};
        Exception last = null;
        for (int attempt = 0; attempt < clients.length; attempt++) {
            try {
                if (context != null && !YtDlpRuntimeManager.isInitialized()) YtDlpRuntimeManager.prepare(context);
                if (attempt == 2 && context != null) YtDlpRuntimeManager.forceUpdate(context);
                YoutubeDLRequest request = new YoutubeDLRequest(url)
                        .addOption("--no-playlist")
                        .addOption("--download-sections", section)
                        .addOption("--force-keyframes-at-cuts")
                        .addOption("--force-overwrites")
                        .addOption("-f", "bestaudio/best")
                        .addOption("-x")
                        .addOption("--audio-format", "wav")
                        .addOption("--no-part")
                        .addOption("-o", output.getAbsolutePath());
                if (!clients[attempt].isEmpty()) request.addOption("--extractor-args", clients[attempt]);
                YoutubeDL.getInstance().execute(request);
                validateWav(output);
                return output;
            } catch (Exception error) {
                last = error;
                if (!isRetryable(error) || attempt == clients.length - 1) break;
                if (output.exists()) output.delete();
            }
        }
        throw new IllegalStateException("Não consegui obter este bloco do vídeo público após atualizar e tentar formatos alternativos.", last);
    }

    public File downloadVideoSection(String url, long startMs, long endMs, File output) throws Exception {
        String section = "*" + seconds(startMs) + "-" + seconds(endMs);
        YoutubeDLRequest request = new YoutubeDLRequest(url)
                .addOption("--no-playlist").addOption("--download-sections", section)
                .addOption("--force-keyframes-at-cuts").addOption("--force-overwrites")
                .addOption("-f", "best").addOption("--no-part").addOption("-o", output.getAbsolutePath());
        YoutubeDL.getInstance().execute(request);
        if (!output.isFile() || output.length() == 0) throw new IllegalStateException("yt-dlp não gerou o trecho de vídeo");
        return output;
    }

    private static void validateWav(File wav) throws Exception {
        if (!wav.isFile() || wav.length() <= 44) throw new IllegalStateException("O áudio do bloco está vazio ou incompleto.");
        try (RandomAccessFile file = new RandomAccessFile(wav, "r")) {
            if (file.readInt() != 0x52494646 || file.readInt() < 0 || file.readInt() != 0x57415645) throw new IllegalStateException("O bloco não é um WAV decodificável.");
            file.seek(40);
            long pcmBytes = Integer.toUnsignedLong(Integer.reverseBytes(file.readInt()));
            if (pcmBytes <= 0) throw new IllegalStateException("O WAV do bloco não contém PCM.");
        }
    }

    private static boolean isRetryable(Exception error) {
        String text = error.getMessage() == null ? "" : error.getMessage().toLowerCase(java.util.Locale.US);
        return text.contains("403") || text.contains("forbidden") || text.contains("sabr")
                || text.contains("signature") || text.contains("challenge") || text.contains("requested format")
                || text.contains("extractor");
    }
    private static String seconds(long ms) { return String.format(java.util.Locale.US, "%.3f", ms / 1000d); }
}
