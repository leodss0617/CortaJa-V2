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
import com.serhat.autosub.ui.main.MainViewModel;

public class AnalysisFragment extends Fragment {
    @Nullable @Override public android.view.View onCreateView(@NonNull android.view.LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        LinearLayout root = new LinearLayout(requireContext()); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(28, 28, 28, 20);
        TextView title = new TextView(requireContext()); title.setText("Analisando vídeo"); title.setTextSize(24); root.addView(title);
        root.addView(new ProgressBar(requireContext()));
        TextView status = new TextView(requireContext()); status.setText("Preparando mídia\n○ Extraindo áudio\n○ Transcrevendo\n○ Entendendo contexto\n○ Encontrando melhores momentos\n○ Preparando resultados"); status.setTextSize(17); root.addView(status);
        MainViewModel vm = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        vm.getShortsAnalyzing().observe(getViewLifecycleOwner(), value -> { if (Boolean.FALSE.equals(value)) status.setText("✓ Análise concluída\nPreparando resultados..."); });
        vm.getShortsError().observe(getViewLifecycleOwner(), value -> { if (value != null && !value.isEmpty()) status.setText("✕ " + value); });
        return root;
    }
}
