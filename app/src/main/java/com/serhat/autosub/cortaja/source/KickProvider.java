package com.serhat.autosub.cortaja.source;

import org.json.JSONObject;

final class KickProvider implements PublicVideoSourceProvider {
    @Override public SourceResolution resolve(PublicVideoSourceResolver.SourceRef ref) throws PublicVideoSourceResolver.SourceResolutionException {
        try {
            JSONObject root=new JSONObject(PublicHttp.get(ref.liveHint?"https://kick.com/api/v2/channels/"+ref.sourceId:"https://kick.com/api/v2/video/"+ref.sourceId));
            String media=find(root,"playback_url","playbackUrl","source","m3u8","playlist");
            JSONObject live=root.optJSONObject("livestream"); if(media.isEmpty()&&live!=null)media=find(live,"playback_url","playbackUrl","source","m3u8");
            if(media.isEmpty())throw new PublicVideoSourceResolver.SourceResolutionException("A Kick não liberou uma playlist pública utilizável para este conteúdo.");
            String title=find(root,"title","name"); if(title.isEmpty())title=ref.liveHint?"Live da Kick":"VOD da Kick";
            return SourceResolution.builder(PublicVideoSourceResolver.Provider.KICK,ref.sourceId).title(title).channel(ref.liveHint?ref.sourceId:"").isLive(ref.liveHint).mediaUrl(media).audioUrl(media).build();
        }catch(PublicVideoSourceResolver.SourceResolutionException e){throw e;}catch(Exception e){throw new PublicVideoSourceResolver.SourceResolutionException("Não foi possível obter a mídia pública da Kick. Verifique se ela está acessível sem login.",e);}
    }
    private static String find(JSONObject o,String...keys){for(String k:keys){String v=o.optString(k,"");if(!v.isEmpty()&&v.startsWith("http"))return v;}for(String k:keys){JSONObject n=o.optJSONObject(k);if(n!=null){String v=find(n,"url","hls","playlist","playback_url");if(!v.isEmpty())return v;}}return "";}
}
