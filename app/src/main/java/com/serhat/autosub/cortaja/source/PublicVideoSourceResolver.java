package com.serhat.autosub.cortaja.source;

import java.net.URI;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PublicVideoSourceResolver {
    public enum Provider { YOUTUBE, TWITCH, KICK }
    public static final class SourceRef {
        public final Provider provider; public final String sourceId; public final boolean liveHint; public final String originalUrl;
        SourceRef(Provider p,String id,boolean live,String url){provider=p;sourceId=id;liveHint=live;originalUrl=url;}
    }
    public static class SourceResolutionException extends RuntimeException { public SourceResolutionException(String m){super(m);} public SourceResolutionException(String m,Throwable c){super(m,c);} }
    public interface Callback { void onResolved(SourceResolution resolution); void onError(String message); }
    private final PublicVideoSourceProvider youtube=new YouTubeProvider();
    private final PublicVideoSourceProvider twitch=new TwitchProvider();
    private final PublicVideoSourceProvider kick=new KickProvider();
    private final ExecutorService executor=Executors.newCachedThreadPool();

    public static Provider providerFor(String url) { return parse(url).provider; }
    public static SourceRef parse(String value) {
        if(value==null||value.trim().isEmpty()) throw new SourceResolutionException("Cole um link público de YouTube, Twitch ou Kick.");
        URI u=URI.create(value.trim()); String host=u.getHost()==null?"":u.getHost().toLowerCase(Locale.US); String path=u.getPath()==null?"":u.getPath();
        if(host.endsWith("youtube.com")||host.equals("youtu.be")) {
            String id=queryParam(u.getRawQuery(), "v"); boolean live=path.startsWith("/live/");
            if(id==null&&live)id=path.substring("/live/".length()).split("/")[0];
            if(id==null&&host.equals("youtu.be"))id=path.substring(1).split("/")[0];
            if(id==null||id.isEmpty())throw new SourceResolutionException("Não foi possível identificar este vídeo do YouTube.");
            return new SourceRef(Provider.YOUTUBE,id,live,value);
        }
        if(host.endsWith("twitch.tv")) {
            String[] parts=path.split("/");
            if(parts.length>=3&&"videos".equalsIgnoreCase(parts[1])) return new SourceRef(Provider.TWITCH,parts[2],false,value);
            String channel=path.replace("/","").trim(); if(channel.isEmpty())throw new SourceResolutionException("Não foi possível identificar este canal da Twitch.");
            return new SourceRef(Provider.TWITCH,channel,true,value);
        }
        if(host.equals("kick.com")||host.endsWith(".kick.com")) {
            String[] parts=path.split("/");
            if(parts.length>=4&&"videos".equalsIgnoreCase(parts[2]))return new SourceRef(Provider.KICK,parts[3],false,value);
            if(parts.length>=3&&"video".equalsIgnoreCase(parts[1]))return new SourceRef(Provider.KICK,parts[2],false,value);
            String channel=path.replace("/","").trim(); if(channel.isEmpty())throw new SourceResolutionException("Não foi possível identificar este canal da Kick.");
            return new SourceRef(Provider.KICK,channel,true,value);
        }
        throw new SourceResolutionException("Plataforma não suportada. Use um link público do YouTube, Twitch ou Kick.");
    }
    public SourceResolution resolve(String url) throws SourceResolutionException { SourceRef ref=parse(url); return provider(ref).resolve(ref); }
    public void resolveAsync(String url,Callback callback){executor.execute(()->{try{SourceResolution r=resolve(url);callback.onResolved(r);}catch(Exception e){callback.onError(e.getMessage()==null?"Não foi possível resolver este link público.":e.getMessage());}});}
    private static String queryParam(String query,String key){if(query==null)return null;for(String part:query.split("\\&")){String[] kv=part.split("=",2);if(kv.length==2&&key.equals(kv[0]))return kv[1];}return null;}
    private PublicVideoSourceProvider provider(SourceRef ref){return ref.provider==Provider.YOUTUBE?youtube:ref.provider==Provider.TWITCH?twitch:kick;}
}
