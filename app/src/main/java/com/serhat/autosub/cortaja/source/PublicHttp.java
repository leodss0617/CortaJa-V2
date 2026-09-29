package com.serhat.autosub.cortaja.source;

import java.io.*; import java.net.*; import java.nio.charset.StandardCharsets;

final class PublicHttp {
    private PublicHttp(){}
    static String get(String url) throws IOException { HttpURLConnection c=(HttpURLConnection)new URL(url).openConnection(); c.setConnectTimeout(12000);c.setReadTimeout(20000);c.setRequestProperty("User-Agent","CortaJa/2.0 public-media"); return read(c); }
    static String postJson(String url,String body) throws IOException { HttpURLConnection c=(HttpURLConnection)new URL(url).openConnection();c.setConnectTimeout(12000);c.setReadTimeout(20000);c.setRequestMethod("POST");c.setDoOutput(true);c.setRequestProperty("Content-Type","application/json");c.setRequestProperty("User-Agent","CortaJa/2.0 public-media");try(OutputStream o=c.getOutputStream()){o.write(body.getBytes(StandardCharsets.UTF_8));}return read(c); }
    private static String read(HttpURLConnection c)throws IOException{int code=c.getResponseCode();InputStream in=code>=400?c.getErrorStream():c.getInputStream();if(in==null)throw new IOException("A plataforma não respondeu.");try(InputStream i=in;Reader r=new InputStreamReader(i,StandardCharsets.UTF_8);StringWriter w=new StringWriter()){char[]b=new char[4096];int n;while((n=r.read(b))>=0)w.write(b,0,n);if(code>=400)throw new IOException("A plataforma recusou este conteúdo público (HTTP "+code+").");return w.toString();}}
}
