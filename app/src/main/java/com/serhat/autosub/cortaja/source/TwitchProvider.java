package com.serhat.autosub.cortaja.source;

import java.net.URLEncoder; import java.nio.charset.StandardCharsets; import java.util.regex.Matcher; import java.util.regex.Pattern;

final class TwitchProvider implements PublicVideoSourceProvider {
    @Override public SourceResolution resolve(PublicVideoSourceResolver.SourceRef ref) throws PublicVideoSourceResolver.SourceResolutionException {
        try {
            String encoded=URLEncoder.encode(ref.sourceId,StandardCharsets.UTF_8.name());
            String media=ref.liveHint?"https://usher.ttvnw.net/api/channel/hls/"+encoded+".m3u8?allow_source=true&allow_audio_only=true&player=twitchweb&fast_bread=true":"https://usher.ttvnw.net/vod/"+ref.sourceId+".m3u8?allow_source=true&allow_audio_only=true&player=twitchweb";
            String html=PublicHttp.get(ref.originalUrl); String title=match(html,"<title[^>]*>(.*?)</title>");
            if(title.isEmpty())title=ref.liveHint?"Live da Twitch":"VOD da Twitch";
            return SourceResolution.builder(PublicVideoSourceResolver.Provider.TWITCH,ref.sourceId).title(clean(title)).channel(ref.liveHint?ref.sourceId:"").isLive(ref.liveHint).mediaUrl(media).audioUrl(media).build();
        }catch(Exception e){throw new PublicVideoSourceResolver.SourceResolutionException("A Twitch não liberou este conteúdo público. Verifique se o canal ou VOD está acessível sem login.",e);}
    }
    private static String match(String text,String regex){Matcher m=Pattern.compile(regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL).matcher(text==null?"":text);return m.find()?m.group(1).trim():"";}
    private static String clean(String t){return t.replace(" - Twitch","").replace("&amp;","&");}
}
