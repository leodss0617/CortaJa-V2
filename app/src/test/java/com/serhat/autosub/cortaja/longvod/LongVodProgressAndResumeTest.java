package com.serhat.autosub.cortaja.longvod;

import com.serhat.autosub.subtitles.SubtitleGenerator;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

public class LongVodProgressAndResumeTest {
    @Test public void activeLongVodPreventsIdleAndResumeAdvancesOneBlock(){
        assertTrue(LongVodJobState.preventsIdle(true));
        assertFalse(LongVodJobState.preventsIdle(false));
        assertEquals(2,LongVodJobState.nextBlockAfter(1));
    }
    @Test public void persistedTranscriptRoundTripsAndDoesNotDuplicateOnResume(){
        List<SubtitleGenerator.SubtitleEntry> first=Arrays.asList(new SubtitleGenerator.SubtitleEntry(1,"00:00:00,000","00:00:01,000","primeiro"),new SubtitleGenerator.SubtitleEntry(2,"00:00:01,000","00:00:02,000","segundo"));
        List<SubtitleGenerator.SubtitleEntry> restored=LongVodTranscriptCodec.decode(LongVodTranscriptCodec.encode(first));
        assertEquals(2,restored.size()); assertEquals("primeiro",restored.get(0).getText()); assertEquals("segundo",restored.get(1).getText());
    }
    @Test public void fakeThirtyMinuteResumeContainsBothBlocks(){
        List<SubtitleGenerator.SubtitleEntry> block0=Arrays.asList(new SubtitleGenerator.SubtitleEntry(1,"00:00:00,000","00:00:01,000","bloco 1"));
        List<SubtitleGenerator.SubtitleEntry> block1=Arrays.asList(new SubtitleGenerator.SubtitleEntry(2,"00:15:00,000","00:15:01,000","bloco 2"));
        List<SubtitleGenerator.SubtitleEntry> all=LongVodTranscriptCodec.decode(LongVodTranscriptCodec.encode(block0));
        all.addAll(LongVodTranscriptCodec.decode(LongVodTranscriptCodec.encode(block1)));
        assertEquals(2,all.size()); assertEquals("bloco 2",all.get(1).getText());
    }
}
