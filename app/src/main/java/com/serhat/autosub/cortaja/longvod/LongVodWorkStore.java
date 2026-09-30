package com.serhat.autosub.cortaja.longvod;

import android.content.Context;
import android.content.SharedPreferences;

import com.serhat.autosub.cortaja.state.LongVodPendingRequest;

/** Small durable active-work record used to recover after service/process recreation. */
public final class LongVodWorkStore {
    private final SharedPreferences prefs;
    public LongVodWorkStore(Context context) { prefs = context.getApplicationContext().getSharedPreferences("cortaja_long_vod_work", Context.MODE_PRIVATE); }
    public synchronized void save(LongVodPendingRequest request) { prefs.edit().putString("project", request.projectId).putString("source", request.sourceUrl).putLong("duration", request.durationMs).putString("version", request.resolverVersion).apply(); }
    public synchronized LongVodPendingRequest load() {
        String source = prefs.getString("source", "");
        if (source == null || source.isEmpty()) return null;
        return new LongVodPendingRequest(prefs.getString("project", ""), source, prefs.getLong("duration", 0), prefs.getString("version", ""));
    }
    public synchronized void clear() { prefs.edit().clear().apply(); }
}
