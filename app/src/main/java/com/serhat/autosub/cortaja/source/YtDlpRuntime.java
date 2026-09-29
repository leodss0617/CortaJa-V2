package com.serhat.autosub.cortaja.source;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class YtDlpRuntime {
    private static final String TAG = "CortaJaYtDlp";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static volatile boolean initialized;

    private YtDlpRuntime() {}

    public static void initializeAsync(Context context) {
        Context app = context.getApplicationContext();
        EXECUTOR.execute(() -> {
            try {
                AndroidYtDlpClient.initialize(app);
                initialized = true;
                Log.i(TAG, "yt-dlp inicializado: " + new AndroidYtDlpClient(app).version());
            } catch (Exception e) {
                Log.w(TAG, "yt-dlp não foi inicializado; o diagnóstico exibirá o erro", e);
            }
        });
    }

    public static synchronized void initializeBlocking(Context context) throws Exception { if (!initialized) { AndroidYtDlpClient.initialize(context.getApplicationContext()); initialized = true; } }
    public static boolean isInitialized() { return initialized; }
}
