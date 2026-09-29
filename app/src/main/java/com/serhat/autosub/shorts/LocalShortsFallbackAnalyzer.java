package com.serhat.autosub.shorts;

import com.serhat.autosub.subtitles.SubtitleGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Deterministic, model-free Shorts selector. It intentionally uses transcript text as the
 * primary signal; audio energy can be added by the caller later without changing this contract.
 */
public final class LocalShortsFallbackAnalyzer {
    private static final Pattern WORD = Pattern.compile("[\\p{L}\\p{N}]{3,}");
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "the", "and", "that", "this", "with", "from", "para", "uma", "que", "com", "como", "por"));
    private static final Set<String> HOOK_WORDS = new HashSet<>(Arrays.asList(
            "why", "how", "what", "secret", "trick", "impossible", "never", "surprising", "question",
            "porquê", "como", "segredo", "truque", "impossível", "surpresa"));
    private static final Set<String> PAYOFF_WORDS = new HashSet<>(Arrays.asList(
            "because", "result", "finally", "answer", "payoff", "worked", "changed", "surprised", "revealed",
            "resultado", "resposta", "funcionou", "mudou", "revelou"));

    public List<ShortsCandidate> analyze(List<SubtitleGenerator.SubtitleEntry> subtitles,
                                          int desiredCount, int minSeconds, int maxSeconds) {
        if (subtitles == null || subtitles.isEmpty()) return Collections.emptyList();
        int desired = Math.max(1, desiredCount);
        long minMs = Math.max(1_000L, minSeconds * 1_000L);
        long maxMs = Math.max(minMs, maxSeconds * 1_000L);
        List<Row> rows = rows(subtitles);
        List<ShortsCandidate> pool = new ArrayList<>();

        // Sentence boundaries are preferred, so the fallback never has to invent a cut inside a thought.
        List<Integer> boundaries = sentenceBoundaries(rows);
        for (int start = 0; start < rows.size(); start++) {
            for (int end = start; end < rows.size() && rows.get(end).end - rows.get(start).start <= maxMs; end++) {
                long duration = rows.get(end).end - rows.get(start).start;
                if (duration < minMs) continue;
                if (!isBoundaryEnd(end, boundaries) && end + 1 < rows.size()) continue;
                pool.add(candidate(rows, start, end));
            }
        }
        if (pool.isEmpty()) {
            int end = rows.size() - 1;
            while (end > 0 && rows.get(end).end - rows.get(0).start > maxMs) end--;
            if (rows.get(end).end - rows.get(0).start >= minMs) pool.add(candidate(rows, 0, end));
        }
        pool.sort(Comparator.comparingInt(ShortsCandidate::getScore).reversed()
                .thenComparingLong(ShortsCandidate::getStartMs));
        return deduplicate(pool, desired);
    }

    private static List<Row> rows(List<SubtitleGenerator.SubtitleEntry> subtitles) {
        List<Row> result = new ArrayList<>();
        for (SubtitleGenerator.SubtitleEntry entry : subtitles) {
            long start = parseTime(entry.getStartTime());
            long end = Math.max(start + 1, parseTime(entry.getEndTime()));
            String text = entry.getText() == null ? "" : entry.getText().trim();
            if (!text.isEmpty()) result.add(new Row(entry.getNumber(), start, end, text));
        }
        return result;
    }

    private static List<Integer> sentenceBoundaries(List<Row> rows) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            String text = rows.get(i).text;
            if (text.matches(".*[.!?。！？؟]$")) result.add(i);
        }
        if (result.isEmpty() || result.get(result.size() - 1) != rows.size() - 1) result.add(rows.size() - 1);
        return result;
    }

    private static boolean isBoundaryEnd(int index, List<Integer> boundaries) {
        return boundaries.contains(index);
    }

    private static ShortsCandidate candidate(List<Row> rows, int start, int end) {
        Row first = rows.get(start);
        Row last = rows.get(end);
        StringBuilder text = new StringBuilder();
        for (int i = start; i <= end; i++) text.append(rows.get(i).text).append(' ');
        String normalized = text.toString().toLowerCase(Locale.ROOT);
        int score = 20;
        score += Math.min(25, countAny(normalized, HOOK_WORDS) * 12);
        score += Math.min(25, countAny(normalized, PAYOFF_WORDS) * 10);
        if (normalized.contains("?") || normalized.contains("¿")) score += 10;
        if (normalized.length() >= 70) score += 10;
        if (normalized.length() <= 700) score += 5;
        score = Math.min(100, score);
        String title = firstWords(first.text, 7);
        return new ShortsCandidate(first.id, last.id, first.start, last.end,
                title, firstWords(first.text, 10), "Local fallback: complete thought with hook/payoff signals", score);
    }

    private static List<ShortsCandidate> deduplicate(List<ShortsCandidate> sorted, int desired) {
        List<ShortsCandidate> result = new ArrayList<>();
        for (ShortsCandidate candidate : sorted) {
            boolean duplicate = false;
            for (ShortsCandidate kept : result) {
                if (overlap(candidate, kept) >= 0.5 || tokenSimilarity(candidate, kept) >= 0.72) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) result.add(candidate);
            if (result.size() == desired) break;
        }
        return result;
    }

    private static double overlap(ShortsCandidate a, ShortsCandidate b) {
        long intersection = Math.max(0, Math.min(a.getEndMs(), b.getEndMs()) - Math.max(a.getStartMs(), b.getStartMs()));
        long shorter = Math.min(a.getDurationMs(), b.getDurationMs());
        return shorter == 0 ? 0 : (double) intersection / shorter;
    }

    private static double tokenSimilarity(ShortsCandidate a, ShortsCandidate b) {
        Set<String> left = tokens(a.getTitle() + " " + a.getHook());
        Set<String> right = tokens(b.getTitle() + " " + b.getHook());
        if (left.isEmpty() || right.isEmpty()) return 0;
        Set<String> intersection = new HashSet<>(left);
        intersection.retainAll(right);
        Set<String> union = new HashSet<>(left);
        union.addAll(right);
        return (double) intersection.size() / union.size();
    }

    private static Set<String> tokens(String value) {
        Set<String> result = new HashSet<>();
        java.util.regex.Matcher matcher = WORD.matcher(value.toLowerCase(Locale.ROOT));
        while (matcher.find()) if (!STOP_WORDS.contains(matcher.group())) result.add(matcher.group());
        return result;
    }

    private static int countAny(String text, Set<String> words) {
        int count = 0;
        for (String word : words) if (text.contains(word)) count++;
        return count;
    }

    private static String firstWords(String text, int maxWords) {
        String[] words = text.trim().split("\\s+");
        if (words.length <= maxWords) return text.trim();
        return String.join(" ", Arrays.copyOf(words, maxWords));
    }

    private static long parseTime(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        String normalized = value.trim().replace(',', '.');
        String[] parts = normalized.split(":");
        try {
            if (parts.length == 3) return (long) ((Double.parseDouble(parts[0]) * 3600 + Double.parseDouble(parts[1]) * 60 + Double.parseDouble(parts[2])) * 1000);
            if (parts.length == 2) return (long) ((Double.parseDouble(parts[0]) * 60 + Double.parseDouble(parts[1])) * 1000);
            return (long) (Double.parseDouble(normalized) * 1000);
        } catch (NumberFormatException ignored) { return 0; }
    }

    private static final class Row {
        final int id; final long start; final long end; final String text;
        Row(int id, long start, long end, String text) { this.id = id; this.start = start; this.end = end; this.text = text; }
    }
}
