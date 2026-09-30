package com.serhat.autosub.cortaja.longvod;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.serhat.autosub.subtitles.SubtitleGenerator;
import java.util.ArrayList;
import java.util.List;

public final class LongVodCheckpointStore extends SQLiteOpenHelper {
    private static final String DB="cortaja_long_vod.db";
    public LongVodCheckpointStore(Context context){super(context,DB,null,2);}
    @Override public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE checkpoints(project_id TEXT PRIMARY KEY, source_id TEXT, block_index INTEGER, start_ms INTEGER, end_ms INTEGER, transcript_persisted INTEGER, resolver_version TEXT, updated_at INTEGER)");
        db.execSQL("CREATE TABLE long_vod_transcripts(project_id TEXT, block_index INTEGER, start_ms INTEGER, end_ms INTEGER, transcript_json TEXT, segment_count INTEGER, created_at INTEGER, PRIMARY KEY(project_id, block_index))");
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){ if(oldVersion<2) db.execSQL("CREATE TABLE IF NOT EXISTS long_vod_transcripts(project_id TEXT, block_index INTEGER, start_ms INTEGER, end_ms INTEGER, transcript_json TEXT, segment_count INTEGER, created_at INTEGER, PRIMARY KEY(project_id, block_index))"); }
    public synchronized void save(LongVodCheckpoint c,String version){
        ContentValues v=new ContentValues(); v.put("project_id",c.projectId);v.put("source_id",c.sourceId);v.put("block_index",c.blockIndex);v.put("start_ms",c.startMs);v.put("end_ms",c.endMs);v.put("transcript_persisted",c.transcriptPersisted?1:0);v.put("resolver_version",version==null?"":version);v.put("updated_at",System.currentTimeMillis());getWritableDatabase().insertWithOnConflict("checkpoints",null,v,SQLiteDatabase.CONFLICT_REPLACE);
    }
    public synchronized void saveTranscript(String projectId,String sourceUrl,LongVodBlock block,List<SubtitleGenerator.SubtitleEntry> entries,String version){
        SQLiteDatabase db=getWritableDatabase(); db.beginTransaction(); try {
            ContentValues t=new ContentValues();t.put("project_id",projectId);t.put("block_index",block.index);t.put("start_ms",block.startMs);t.put("end_ms",block.endMs);t.put("transcript_json",LongVodTranscriptCodec.encode(entries));t.put("segment_count",entries==null?0:entries.size());t.put("created_at",System.currentTimeMillis());db.insertWithOnConflict("long_vod_transcripts",null,t,SQLiteDatabase.CONFLICT_REPLACE);
            ContentValues c=new ContentValues();c.put("project_id",projectId);c.put("source_id",sourceUrl);c.put("block_index",block.index);c.put("start_ms",block.startMs);c.put("end_ms",block.endMs);c.put("transcript_persisted",1);c.put("resolver_version",version==null?"":version);c.put("updated_at",System.currentTimeMillis());db.insertWithOnConflict("checkpoints",null,c,SQLiteDatabase.CONFLICT_REPLACE);db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    public synchronized List<SubtitleGenerator.SubtitleEntry> loadTranscripts(String projectId){
        List<SubtitleGenerator.SubtitleEntry> out=new ArrayList<>(); Cursor c=getReadableDatabase().query("long_vod_transcripts",new String[]{"transcript_json"},"project_id=?",new String[]{projectId},null,null,"block_index ASC");
        try {while(c.moveToNext()) out.addAll(LongVodTranscriptCodec.decode(c.getString(0)));} finally {c.close();} return out;
    }
    public synchronized LongVodCheckpoint latest(String projectId){try(Cursor c=getReadableDatabase().query("checkpoints",null,"project_id=?",new String[]{projectId},null,null,"block_index DESC","1")){if(!c.moveToFirst())return null;return new LongVodCheckpoint(c.getString(c.getColumnIndexOrThrow("project_id")),c.getString(c.getColumnIndexOrThrow("source_id")),c.getInt(c.getColumnIndexOrThrow("block_index")),c.getLong(c.getColumnIndexOrThrow("start_ms")),c.getLong(c.getColumnIndexOrThrow("end_ms")),c.getInt(c.getColumnIndexOrThrow("transcript_persisted"))!=0);}}
}
