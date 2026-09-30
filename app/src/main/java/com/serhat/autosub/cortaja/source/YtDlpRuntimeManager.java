package com.serhat.autosub.cortaja.source;

import android.content.Context;
import android.content.SharedPreferences;

/** Owns yt-dlp initialization and the bounded update policy for the whole app. */
public final class YtDlpRuntimeManager {
    private static final String PREFS = "cortaja_ytdlp_runtime";
    private static final String LAST_UPDATE = "last_update_ms";
    private static final String CHANNEL = "channel";
    private static final long UPDATE_INTERVAL_MS = 24L * 60L * 60L * 1000L;
    private static final Object LOCK = new Object();
    private static volatile boolean initialized;
    private static volatile String lastState = "não preparado";

    private YtDlpRuntimeManager() {}

    public static void prepare(Context context) throws Exception {
        synchronized (LOCK) {
            Context app = context.getApplicationContext();
            AndroidYtDlpClient.initialize(app);
            initialized = true;
            SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            long last = prefs.getLong(LAST_UPDATE, 0L);
            if (System.currentTimeMillis() - last >= UPDATE_INTERVAL_MS) updateLocked(app, prefs);
            else lastState = "atualizado recentemente";
        }
    }

    public static void forceUpdate(Context context) throws Exception {
        synchronized (LOCK) {
            Context app = context.getApplicationContext();
            AndroidYtDlpClient.initialize(app);
            initialized = true;
            updateLocked(app, app.getSharedPreferences(PREFS, Context.MODE_PRIVATE));
        }
    }

    private static void updateLocked(Context app, SharedPreferences prefs) throws Exception {
        String channel = "stable";
        try {
            new AndroidYtDlpClient(app).updateStable();
        } catch (Exception stableError) {
            channel = "nightly";
            new AndroidYtDlpClient(app).updateNightly();
        }
        prefs.edit().putLong(LAST_UPDATE, System.currentTimeMillis()).putString(CHANNEL, channel).apply();
        lastState = "atualizado";
    }

    public static String version(Context context) { return new AndroidYtDlpClient(context).version(); }
    public static long lastUpdate(Context context) { return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(LAST_UPDATE, 0L); }
    public static String channel(Context context) { return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(CHANNEL, "não atualizado"); }
    public static String state() { return lastState; }
    public static boolean isInitialized() { return initialized; }
}
