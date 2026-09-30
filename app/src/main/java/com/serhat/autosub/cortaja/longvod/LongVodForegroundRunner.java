package com.serhat.autosub.cortaja.longvod;

import android.content.Context;
import android.net.Uri;
import com.serhat.autosub.cortaja.source.YtDlpSectionDownloader;
import com.serhat.autosub.subtitles.SubtitleGenerator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public final class LongVodForegroundRunner {
    public interface Listener {
        void onProgress(LongVodBlock block,int completed,int total);
        void onComplete(List<SubtitleGenerator.SubtitleEntry> entries);
        void onError(String message);
        default void onStage(LongVodStage stage,int blockIndex,int totalBlocks,int blockProgress,int overallProgress,String message) {}
        default void onWhisperProgress(int progress) {}
    }
    private final SubtitleGenerator generator; private final LongVodCheckpointStore checkpoints; private final YtDlpSectionDownloader downloader;
    private volatile boolean cancelled;
    public void cancel() { cancelled = true; generator.cancelGeneration(); }
    public LongVodForegroundRunner(SubtitleGenerator g,LongVodCheckpointStore c){this(g,c,null);}
    public LongVodForegroundRunner(SubtitleGenerator g,LongVodCheckpointStore c,Context context){generator=g;checkpoints=c;downloader=new YtDlpSectionDownloader(context);}
    public void run(String projectId,String sourceUrl,long durationMs,String resolverVersion,File workDir,Listener listener){
        new Thread(() -> {
            List<SubtitleGenerator.SubtitleEntry> all=new ArrayList<>();
            try {
                List<LongVodBlock> blocks=new LongVodPlanner(15).plan(durationMs);
                if(blocks.isEmpty()){listener.onStage(LongVodStage.FAILED,0,0,0,0,"Não foi possível determinar a duração deste VOD.");listener.onError("Não foi possível determinar a duração deste VOD.");return;}
                all.addAll(checkpoints.loadTranscripts(projectId));
                LongVodCheckpoint checkpoint=checkpoints.latest(projectId); int first=new LongVodPlanner(15).nextBlockIndex(blocks,checkpoint);
                if(!workDir.exists()&&!workDir.mkdirs())throw new IllegalStateException("Não foi possível preparar o armazenamento temporário");
                for(int i=first;i<blocks.size();i++){
                    if (cancelled) { listener.onError("Análise cancelada"); return; }
                    LongVodBlock block=blocks.get(i); File audio=new File(workDir,"bloco-"+block.index+".wav");
                    int overall=i*100/blocks.size(); listener.onStage(LongVodStage.PREPARING_BLOCK,i,blocks.size(),0,overall,"Preparando bloco "+(i+1)+" de "+blocks.size());
                    listener.onStage(LongVodStage.DOWNLOADING_AUDIO,i,blocks.size(),0,overall,"Baixando áudio do bloco "+(i+1)+" de "+blocks.size());
                    downloader.downloadAudioSection(sourceUrl,block.startMs,block.endMs,audio);
                    listener.onStage(LongVodStage.VALIDATING_AUDIO,i,blocks.size(),100,overall,"Validando áudio");
                    CountDownLatch done=new CountDownLatch(1); List<SubtitleGenerator.SubtitleEntry>[] result=new List[]{new ArrayList<>()}; String[] error=new String[]{null};
                    listener.onStage(LongVodStage.TRANSCRIBING,i,blocks.size(),0,overall,"Transcrevendo bloco "+(i+1)+" de "+blocks.size());
                    generator.generateSubtitles(Uri.fromFile(audio),"",new SubtitleGenerator.SubtitleGenerationCallback(){
                        public void onPartialSubtitlesGenerated(List<SubtitleGenerator.SubtitleEntry> ignored){}
                        public void onSubtitlesGenerated(List<SubtitleGenerator.SubtitleEntry> entries){result[0]=offset(entries,block.startMs);done.countDown();}
                        public void onError(String message){error[0]=message;done.countDown();}
                        public void onProgressUpdate(int progress){listener.onWhisperProgress(progress); int p=progress<0?0:Math.min(100,progress); listener.onStage(LongVodStage.TRANSCRIBING,block.index,blocks.size(),p,Math.min(99,(block.index*100+p)/blocks.size()),"Transcrevendo bloco "+(block.index+1)+" de "+blocks.size()+" • "+p+"%");}
                        public void onCancelled(){error[0]="Análise cancelada";done.countDown();}
                    });
                    done.await(); if(error[0]!=null)throw new IllegalStateException(error[0]);
                    listener.onStage(LongVodStage.SAVING_TRANSCRIPT,i,blocks.size(),100,Math.min(99,(i+1)*100/blocks.size()),"Salvando transcrição");
                    checkpoints.saveTranscript(projectId,sourceUrl,block,result[0],resolverVersion); all.addAll(result[0]);
                    if(!audio.delete())audio.deleteOnExit(); listener.onProgress(block,i+1,blocks.size());
                }
                listener.onStage(LongVodStage.ANALYZING_MOMENTS,blocks.size(),blocks.size(),100,100,"Procurando melhores momentos");
                listener.onStage(LongVodStage.RANKING,blocks.size(),blocks.size(),100,100,"Classificando cortes");
                listener.onStage(LongVodStage.COMPLETED,blocks.size(),blocks.size(),100,100,"Análise concluída"); listener.onComplete(all);
            }catch(Exception e){listener.onStage(LongVodStage.FAILED,0,0,0,0,e.getMessage()==null?"Não foi possível analisar este VOD longo.":e.getMessage());listener.onError(e.getMessage()==null?"Não foi possível analisar este VOD longo.":e.getMessage());}
        },"cortaja-vod-blocos").start();
    }
    private static List<SubtitleGenerator.SubtitleEntry> offset(List<SubtitleGenerator.SubtitleEntry> source,long offsetMs){List<SubtitleGenerator.SubtitleEntry> r=new ArrayList<>();if(source==null)return r;for(SubtitleGenerator.SubtitleEntry e:source)r.add(new SubtitleGenerator.SubtitleEntry(e.getNumber(),shift(e.getStartTime(),offsetMs),shift(e.getEndTime(),offsetMs),e.getText(),e.getWords()));return r;}
    private static String shift(String value,long offsetMs){String[] p=value.replace(',', '.').split(":" );long ms=(Long.parseLong(p[0])*3600+Long.parseLong(p[1])*60)*1000;String[] s=p[2].split("\\.");ms+=Long.parseLong(s[0])*1000+(s.length>1?Long.parseLong((s[1]+"000").substring(0,3)):0)+offsetMs;long h=ms/3600000;ms%=3600000;long m=ms/60000;ms%=60000;long sec=ms/1000;long x=ms%1000;return String.format(java.util.Locale.US,"%02d:%02d:%02d,%03d",h,m,sec,x);}
}
