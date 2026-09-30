package com.serhat.autosub.cortaja.longvod;

public final class LongVodJobState {
    public static boolean preventsIdle(boolean running) { return running; }
    public static int nextBlockAfter(int completedBlock) { return completedBlock + 1; }
    public enum Status { PREPARING, RUNNING, ANALYZING, COMPLETED, FAILED, CANCELLED }
    public final String projectId, sourceUrl, provider, title, resolverVersion, stageMessage, error;
    public final long durationMs, processedMs, lastActivityAt;
    public final int currentBlock, totalBlocks, blockProgress, overallProgress, transcriptSegments, candidateCount;
    public final Status status;
    public final LongVodStage stage;
    public LongVodJobState(String projectId, String sourceUrl, String provider, String title, long durationMs,
            Status status, LongVodStage stage, int currentBlock, int totalBlocks, int blockProgress,
            int overallProgress, long processedMs, int transcriptSegments, int candidateCount,
            long lastActivityAt, String resolverVersion, String stageMessage, String error) {
        this.projectId=projectId; this.sourceUrl=sourceUrl; this.provider=provider; this.title=title;
        this.durationMs=durationMs; this.status=status; this.stage=stage; this.currentBlock=currentBlock;
        this.totalBlocks=totalBlocks; this.blockProgress=blockProgress; this.overallProgress=overallProgress;
        this.processedMs=processedMs; this.transcriptSegments=transcriptSegments; this.candidateCount=candidateCount;
        this.lastActivityAt=lastActivityAt; this.resolverVersion=resolverVersion; this.stageMessage=stageMessage; this.error=error;
    }
}
