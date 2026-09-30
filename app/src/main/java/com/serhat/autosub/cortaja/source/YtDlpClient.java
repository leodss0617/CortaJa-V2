package com.serhat.autosub.cortaja.source;

public interface YtDlpClient {
    YtDlpMetadata getInfo(String url) throws Exception;
    default YtDlpMetadata getInfo(String url, String playerClient) throws Exception { return getInfo(url); }
    void updateStable() throws Exception;
    void updateNightly() throws Exception;
    String version();
}
