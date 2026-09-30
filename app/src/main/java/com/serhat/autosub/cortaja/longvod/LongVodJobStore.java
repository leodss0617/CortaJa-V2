package com.serhat.autosub.cortaja.longvod;

import android.content.Context;
import android.content.SharedPreferences;

public final class LongVodJobStore {
    private final SharedPreferences prefs;
    public LongVodJobStore(Context context) { prefs=context.getApplicationContext().getSharedPreferences("cortaja_long_vod_job", Context.MODE_PRIVATE); }
    public synchronized void save(LongVodJobState s) {
        prefs.edit().putString("project",s.projectId).putString("source",s.sourceUrl).putString("provider",s.provider)
            .putString("title",s.title).putLong("duration",s.durationMs).putString("status",s.status.name())
            .putString("stage",s.stage.name()).putInt("block",s.currentBlock).putInt("total",s.totalBlocks)
            .putInt("blockProgress",s.blockProgress).putInt("overall",s.overallProgress).putLong("processed",s.processedMs)
            .putInt("segments",s.transcriptSegments).putInt("candidates",s.candidateCount).putLong("activity",s.lastActivityAt)
            .putString("version",s.resolverVersion).putString("message",s.stageMessage).putString("error",s.error).apply();
    }
    public synchronized LongVodJobState load() {
        String source=prefs.getString("source",""); if(source==null||source.isEmpty()) return null;
        return new LongVodJobState(prefs.getString("project",""),source,prefs.getString("provider",""),prefs.getString("title",""),
            prefs.getLong("duration",0), LongVodJobState.Status.valueOf(prefs.getString("status",LongVodJobState.Status.RUNNING.name())),
            LongVodStage.valueOf(prefs.getString("stage",LongVodStage.PREPARING_BLOCK.name())),prefs.getInt("block",0),prefs.getInt("total",0),
            prefs.getInt("blockProgress",0),prefs.getInt("overall",0),prefs.getLong("processed",0),prefs.getInt("segments",0),
            prefs.getInt("candidates",0),prefs.getLong("activity",0),prefs.getString("version",""),prefs.getString("message",""),prefs.getString("error",""));
    }
    public synchronized void clear(){ prefs.edit().clear().apply(); }
}
