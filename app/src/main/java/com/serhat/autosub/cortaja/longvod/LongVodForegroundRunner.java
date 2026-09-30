package com.serhat.autosub.cortaja.longvod;

import android.net.Uri;

import com.serhat.autosub.cortaja.source.YtDlpSectionDownloader;
import com.serhat.autosub.subtitles.SubtitleGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/** Sequential chunk runner. Only the current section and its PCM are held by the generator. */
public final class LongVodForegroundRunner {
    public interface Listener { void onProgress(LongVodBlock block, int completed, int total); void onComplete(List<SubtitleGenerator.SubtitleEntry> entries); void onError(String message); }
    private final SubtitleGenerator generator;
    private final LongVodCheckpointStore checkpoints;
    private final YtDlpSectionDownloader downloader = new YtDlpSectionDownloader();

    public LongVodForegroundRunner(SubtitleGenerator generator, LongVodCheckpointStore checkpoints) {
        this.generator = generator; this.checkpoints = checkpoints;
    }

    public void run(String projectId, String sourceUrl, long durationMs, String resolverVersion, File workDir, Listener listener) {
        new Thread(() -> {
            List<SubtitleGenerator.SubtitleEntry> all = new ArrayList<>();
            try {
                List<LongVodBlock> blocks = new LongVodPlanner(15).plan(durationMs);
                if (blocks.isEmpty()) { listener.onError("Não foi possível determinar a duração deste VOD."); return; }
                LongVodCheckpoint checkpoint = checkpoints.latest(projectId);
                int first = new LongVodPlanner(15).nextBlockIndex(blocks, checkpoint);
                if (!workDir.exists() && !workDir.mkdirs()) throw new IllegalStateException("Não foi possível preparar o armazenamento temporário");
                for (int i = first; i < blocks.size(); i++) {
                    LongVodBlock block = blocks.get(i);
                    File audio = new File(workDir, "bloco-" + block.index + ".wav");
                    downloader.downloadAudioSection(sourceUrl, block.startMs, block.endMs, audio);
                    CountDownLatch done = new CountDownLatch(1);
                    List<SubtitleGenerator.SubtitleEntry>[] result = new List[]{new ArrayList<>()};
                    String[] error = new String[]{null};
                    generator.generateSubtitles(Uri.fromFile(audio), "", new SubtitleGenerator.SubtitleGenerationCallback() {
                        public void onPartialSubtitlesGenerated(List<SubtitleGenerator.SubtitleEntry> ignored) {}
                        public void onSubtitlesGenerated(List<SubtitleGenerator.SubtitleEntry> entries) { result[0] = offset(entries, block.startMs); done.countDown(); }
                        public void onError(String message) { error[0] = message; done.countDown(); }
                        public void onProgressUpdate(int progress) {}
                        public void onCancelled() { error[0] = "Análise cancelada"; done.countDown(); }
                    });
                    done.await();
                    if (error[0] != null) throw new IllegalStateException(error[0]);
                    all.addAll(result[0]);
                    checkpoints.save(new LongVodCheckpoint(projectId, sourceUrl, block.index, block.startMs, block.endMs, true), resolverVersion);
                    if (!audio.delete()) audio.deleteOnExit();
                    listener.onProgress(block, i + 1, blocks.size());
                }
                listener.onComplete(all);
            } catch (Exception e) { listener.onError(e.getMessage() == null ? "Não foi possível analisar este VOD longo." : e.getMessage()); }
        }, "cortaja-vod-blocos").start();
    }

    private static List<SubtitleGenerator.SubtitleEntry> offset(List<SubtitleGenerator.SubtitleEntry> source, long offsetMs) {
        List<SubtitleGenerator.SubtitleEntry> result = new ArrayList<>();
        for (SubtitleGenerator.SubtitleEntry entry : source) {
            result.add(new SubtitleGenerator.SubtitleEntry(entry.getNumber(), shift(entry.getStartTime(), offsetMs), shift(entry.getEndTime(), offsetMs), entry.getText(), entry.getWords()));
        }
        return result;
    }

    private static String shift(String value, long offsetMs) {
        String[] parts = value.replace(',', '.').split(":");
        long ms = (Long.parseLong(parts[0]) * 3600 + Long.parseLong(parts[1]) * 60) * 1000;
        String[] sec = parts[2].split("\\.");
        ms += Long.parseLong(sec[0]) * 1000 + (sec.length > 1 ? Long.parseLong((sec[1] + "000").substring(0, 3)) : 0);
        ms += offsetMs;
        long h = ms / 3_600_000; ms %= 3_600_000; long m = ms / 60_000; ms %= 60_000; long s = ms / 1000; long x = ms % 1000;
        return String.format(java.util.Locale.US, "%02d:%02d:%02d,%03d", h, m, s, x);
    }
}
