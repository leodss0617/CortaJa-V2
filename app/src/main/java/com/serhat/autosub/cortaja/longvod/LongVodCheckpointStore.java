package com.serhat.autosub.cortaja.longvod;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Durable checkpoint ledger; each completed block is committed before the next starts. */
public final class LongVodCheckpointStore extends SQLiteOpenHelper {
    private static final String DB = "cortaja_long_vod.db";
    public LongVodCheckpointStore(Context context) { super(context, DB, null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE checkpoints(project_id TEXT PRIMARY KEY, source_id TEXT, block_index INTEGER, start_ms INTEGER, end_ms INTEGER, transcript_persisted INTEGER, resolver_version TEXT, updated_at INTEGER)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public synchronized void save(LongVodCheckpoint checkpoint, String resolverVersion) {
        ContentValues values = new ContentValues();
        values.put("project_id", checkpoint.projectId); values.put("source_id", checkpoint.sourceId);
        values.put("block_index", checkpoint.blockIndex); values.put("start_ms", checkpoint.startMs);
        values.put("end_ms", checkpoint.endMs); values.put("transcript_persisted", checkpoint.transcriptPersisted ? 1 : 0);
        values.put("resolver_version", resolverVersion == null ? "" : resolverVersion); values.put("updated_at", System.currentTimeMillis());
        getWritableDatabase().insertWithOnConflict("checkpoints", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public synchronized LongVodCheckpoint latest(String projectId) {
        try (Cursor c = getReadableDatabase().query("checkpoints", null, "project_id=?", new String[]{projectId}, null, null, "block_index DESC", "1")) {
            if (!c.moveToFirst()) return null;
            return new LongVodCheckpoint(c.getString(c.getColumnIndexOrThrow("project_id")), c.getString(c.getColumnIndexOrThrow("source_id")),
                    c.getInt(c.getColumnIndexOrThrow("block_index")), c.getLong(c.getColumnIndexOrThrow("start_ms")),
                    c.getLong(c.getColumnIndexOrThrow("end_ms")), c.getInt(c.getColumnIndexOrThrow("transcript_persisted")) != 0);
        }
    }
}
