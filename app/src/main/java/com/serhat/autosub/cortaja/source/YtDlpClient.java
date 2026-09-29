package com.serhat.autosub.cortaja.source;

public interface YtDlpClient {
    YtDlpMetadata getInfo(String url) throws Exception;
    void updateStable() throws Exception;
    void updateNightly() throws Exception;
    String version();
}
