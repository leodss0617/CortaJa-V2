package com.serhat.autosub.cortaja.state;

import com.serhat.autosub.shorts.ShortsProject;

public final class CortaJaAnalysisState {
    private CortaJaAnalysisState() {}

    public static boolean isSuccess(ShortsProject project, String error) {
        return error == null || error.trim().isEmpty()
                ? project != null && project.getCandidates() != null && !project.getCandidates().isEmpty()
                : false;
    }

    public static String errorText(String error) {
        return error == null || error.trim().isEmpty() ? "" : "✕ " + error;
    }
}
