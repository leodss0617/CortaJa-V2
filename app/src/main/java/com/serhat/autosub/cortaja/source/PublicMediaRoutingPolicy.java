package com.serhat.autosub.cortaja.source;

import android.net.Uri;

/** Keeps public HTTP media out of the SAF/local-file queue. */
public final class PublicMediaRoutingPolicy {
    public enum Route { PUBLIC_YTDLP, LOCAL_FILE, LIVE_UNAVAILABLE, INVALID }
    private PublicMediaRoutingPolicy() {}

    public static Route route(SourceResolution resolution) {
        if (resolution == null) return Route.INVALID;
        String source = first(resolution.sourceUrl, first(resolution.mediaUrl, resolution.audioUrl));
        if (isLocal(source)) return Route.LOCAL_FILE;
        if (!isHttp(source)) return Route.INVALID;
        if (resolution.isLive) return Route.LIVE_UNAVAILABLE;
        return resolution.durationMs > 0 ? Route.PUBLIC_YTDLP : Route.INVALID;
    }

    public static boolean isRemote(String value) { return isHttp(value); }

    public static boolean isRemote(Uri uri) {
        return uri != null && isRemote(uri.toString());
    }

    private static boolean isLocal(String value) {
        String scheme = scheme(value);
        return "content".equalsIgnoreCase(scheme) || "file".equalsIgnoreCase(scheme);
    }
    private static boolean isHttp(String value) {
        String scheme = scheme(value);
        return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
    }
    private static String scheme(String value) {
        if (value == null) return "";
        int separator = value.indexOf(':');
        return separator <= 0 ? "" : value.substring(0, separator);
    }
    private static String first(String value, String fallback) { return value == null || value.isEmpty() ? fallback : value; }
}
