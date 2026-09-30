package com.serhat.autosub.cortaja.ui;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serhat.autosub.cortaja.state.CortaJaAnalysisState;
import com.serhat.autosub.ui.main.MainViewModel;

public class AnalysisFragment extends Fragment {
    @Nullable @Override public android.view.View onCreateView(@NonNull android.view.LayoutInflater i,
                                                               @Nullable ViewGroup c,
                                                               @Nullable Bundle b) {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 28, 28, 20);
        TextView title = new TextView(requireContext()); title.setText("Analisando vídeo"); title.setTextSize(24); root.addView(title);
        root.addView(new ProgressBar(requireContext()));
        TextView status = new TextView(requireContext()); status.setTextSize(17); root.addView(status);
        MainViewModel vm = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        Runnable render = () -> {
            String error = vm.getShortsError().getValue();
            if (error != null && !error.trim().isEmpty()) {
                status.setText(CortaJaAnalysisState.errorText(error));
                return;
            }
            if (CortaJaAnalysisState.isSuccess(vm.getShortsProject().getValue(), error)) {
                status.setText("✓ Análise concluída\nPreparando resultados...");
                return;
            }
            String pipeline = vm.getGeneralStatusText().getValue();
            status.setText(pipeline == null || pipeline.trim().isEmpty()
                    ? "Preparando análise..." : pipeline);
        };
        vm.getShortsError().observe(getViewLifecycleOwner(), value -> render.run());
        vm.getShortsProject().observe(getViewLifecycleOwner(), value -> render.run());
        vm.getGeneralStatusText().observe(getViewLifecycleOwner(), value -> render.run());
        render.run();
        return root;
    }
}
