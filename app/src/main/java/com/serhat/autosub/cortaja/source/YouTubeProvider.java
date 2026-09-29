package com.serhat.autosub.cortaja.source;

import org.json.JSONArray; import org.json.JSONObject;

final class YouTubeProvider implements PublicVideoSourceProvider {
    @Override public SourceResolution resolve(PublicVideoSourceResolver.SourceRef ref) throws PublicVideoSourceResolver.SourceResolutionException {
        try {
            String body="{\"videoId\":\""+ref.sourceId+"\",\"context\":{\"client\":{\"clientName\":\"ANDROID\",\"clientVersion\":\"19.09.37\"}}}";
            JSONObject root=new JSONObject(PublicHttp.postJson("https://www.youtube.com/youtubei/v1/player?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8https://www.youtube.com/youtubei/v1/player?prettyPrint=falseprettyPrint=false",body));
            JSONObject details=root.optJSONObject("videoDetails"); JSONObject play=root.optJSONObject("streamingData");
            if(details==null||play==null)throw new PublicVideoSourceResolver.SourceResolutionException("O YouTube não liberou uma mídia pública utilizável para este vídeo.");
            String hls=play.optString("hlsManifestUrl",""); String media=hls;
            if(media.isEmpty()){JSONArray formats=play.optJSONArray("formats");if(formats!=null)for(int i=0;i<formats.length();i++){String u=formats.optJSONObject(i).optString("url","");if(!u.isEmpty()){media=u;break;}}}
            if(media.isEmpty()){JSONArray adaptive=play.optJSONArray("adaptiveFormats");if(adaptive!=null)for(int i=0;i<adaptive.length();i++){String u=adaptive.optJSONObject(i).optString("url","");if(!u.isEmpty()){media=u;break;}}}
            if(media.isEmpty())throw new PublicVideoSourceResolver.SourceResolutionException("O YouTube exige uma sessão ou restringe este conteúdo. Escolha outro vídeo público.");
            long duration=0;try{duration=Long.parseLong(details.optString("lengthSeconds","0"))*1000L;}catch(Exception ignored){}
            return SourceResolution.builder(PublicVideoSourceResolver.Provider.YOUTUBE,ref.sourceId).title(details.optString("title","Vídeo do YouTube")).channel(details.optString("author","")).thumbnail("https://i.ytimg.com/vi/"+ref.sourceId+"/hqdefault.jpg").durationMs(duration).isLive(ref.liveHint).mediaUrl(media).audioUrl(media).build();
        }catch(PublicVideoSourceResolver.SourceResolutionException e){throw e;}catch(Exception e){throw new PublicVideoSourceResolver.SourceResolutionException("Não foi possível obter a mídia pública do YouTube. Verifique se o vídeo está disponível sem login.",e);}
    }
}
