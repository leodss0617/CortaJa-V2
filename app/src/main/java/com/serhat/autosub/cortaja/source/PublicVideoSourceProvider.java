package com.serhat.autosub.cortaja.source;

public interface PublicVideoSourceProvider {
    SourceResolution resolve(PublicVideoSourceResolver.SourceRef source) throws PublicVideoSourceResolver.SourceResolutionException;
}
