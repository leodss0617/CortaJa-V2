package com.serhat.autosub.cortaja.longvod;

import com.serhat.autosub.subtitles.SubtitleGenerator;
import java.util.ArrayList;
import java.util.List;

public final class LongVodTranscriptCodec {
    private LongVodTranscriptCodec() {}
    public static String encode(List<SubtitleGenerator.SubtitleEntry> entries){StringBuilder b=new StringBuilder();if(entries!=null)for(SubtitleGenerator.SubtitleEntry e:entries)b.append(e.getNumber()).append("|").append(clean(e.getStartTime())).append("|").append(clean(e.getEndTime())).append("|").append(clean(e.getText())).append(";");return b.toString();}
    public static List<SubtitleGenerator.SubtitleEntry> decode(String raw){List<SubtitleGenerator.SubtitleEntry> out=new ArrayList<>();if(raw==null)return out;for(String line:raw.split(";")){if(line.isEmpty())continue;String[] p=line.split("[|]",4);if(p.length==4)out.add(new SubtitleGenerator.SubtitleEntry(Integer.parseInt(p[0]),p[1],p[2],p[3]));}return out;}
    private static String clean(String s){return s==null?"":s.replace("|"," ").replace(";"," ");}
}
