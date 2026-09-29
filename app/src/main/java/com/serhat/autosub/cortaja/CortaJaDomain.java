package com.serhat.autosub.cortaja;

import java.util.Locale;
import java.util.regex.Pattern;

/** Small product-facing contract kept independent from the legacy AutoSub UI. */
public final class CortaJaDomain {
    private static final Pattern YOUTUBE = Pattern.compile(
            "^https?://(www\\.)?(youtube\\.com/watch\\?v=[^&\\s]+|youtu\\.be/[^?\\s]+)([?&].*)?$",
            Pattern.CASE_INSENSITIVE);
    private CortaJaDomain() { }
    public static boolean isValidYoutubeUrl(String value) { return value != null && YOUTUBE.matcher(value.trim()).matches(); }
    public enum Navigation {
        INICIO("Início"), PROJETOS("Projetos"), MEUS_CORTES("Meus Cortes"), CONFIGURACOES("Configurações");
        public final String label;
        Navigation(String label) { this.label = label; }
    }
    public static final class Candidate {
        public final int ranking; public final String category; public final int score;
        public final long startMs; public final long endMs; public final String reason;
        public Candidate(int ranking, String category, int score, long startMs, long endMs, String reason) {
            this.ranking = ranking; this.category = category == null ? "Melhores momentos" : category;
            this.score = Math.max(0, Math.min(100, score)); this.startMs = Math.max(0, startMs);
            this.endMs = Math.max(this.startMs, endMs); this.reason = reason == null ? "" : reason;
        }
        public long durationMs() { return endMs - startMs; }
        public String scoreLabel() { return String.format(Locale.forLanguageTag("pt-BR"), "%.1f/10", score / 10.0); }
    }
    public static final class ProjectRequest {
        public final String source; public final int desiredCount; public final int minDurationSeconds;
        public final int maxDurationSeconds; public final String goal; public final String format;
        public final int quality; public final boolean captions; public final String captionStyle;
        public ProjectRequest(String source, int desiredCount, int minDurationSeconds, int maxDurationSeconds,
                              String goal, String format, int quality, boolean captions, String captionStyle) {
            this.source = source == null ? "" : source; this.desiredCount = desiredCount;
            this.minDurationSeconds = minDurationSeconds; this.maxDurationSeconds = maxDurationSeconds;
            this.goal = goal == null ? "Melhores momentos" : goal; this.format = format == null ? "9:16" : format;
            this.quality = quality; this.captions = captions; this.captionStyle = captionStyle == null ? "Clean" : captionStyle;
        }
    }
}
