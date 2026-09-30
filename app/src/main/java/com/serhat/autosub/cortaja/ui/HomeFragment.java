package com.serhat.autosub.cortaja.ui;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serhat.autosub.ui.main.MainActivity;
import com.serhat.autosub.ui.main.MainViewModel;
import com.serhat.autosub.service.AutoSubTaskState;

public class HomeFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull android.view.LayoutInflater i,
                                                  @Nullable ViewGroup c,
                                                  @Nullable android.os.Bundle b) {
        LinearLayout root = column();
        root.setPadding(28, 28, 28, 20);
        root.addView(text("CortaJá", 30));
        root.addView(text("Transforme vídeos longos nos melhores momentos.", 18));
        EditText link = new EditText(requireContext());
        link.setHint("Cole o link do vídeo, VOD ou live");
        link.setSingleLine(true);
        root.addView(link);
        Button analyze = button("ANALISAR E GERAR CORTES");
        analyze.setOnClickListener(v -> {
            String value = link.getText().toString().trim();
            if (value.isEmpty()) { link.setError("Cole um link público"); return; }
            ((MainActivity) requireActivity()).startCortaJaLinkAnalysis(value);
        });
        root.addView(analyze);
        Button newProject = button("+ NOVO PROJETO");
        newProject.setOnClickListener(v -> ((MainActivity) requireActivity()).openNewProject());
        root.addView(newProject);
        root.addView(text("IA LOCAL", 14));
        TextView whisper = text("Whisper • Preparando...", 16);
        TextView activeJob = text("", 16); root.addView(activeJob);
        root.addView(whisper);
        root.addView(text("Gemma • será usada quando compatível; fallback local disponível", 16));
        root.addView(text("PROJETOS RECENTES", 14));
        root.addView(text("Seus projetos aparecerão aqui.", 16));

        MainViewModel vm = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        Runnable updateWhisper = () -> {
            Boolean ready = vm.getModelReady().getValue();
            String modelStatus = vm.getModelStatusText().getValue();
            String generalStatus = vm.getGeneralStatusText().getValue();
            boolean error = containsError(modelStatus) || containsError(generalStatus);
            whisper.setText(Boolean.TRUE.equals(ready) ? "Whisper • Pronto"
                    : error ? "Whisper • Erro" : "Whisper • Preparando...");
        };
        vm.getModelReady().observe(getViewLifecycleOwner(), value -> updateWhisper.run());
        vm.getCurrentTaskState().observe(getViewLifecycleOwner(), state -> { if (state != null && state.getTaskType() == AutoSubTaskState.TaskType.SUBTITLE_GENERATION) activeJob.setText("ANÁLISE EM ANDAMENTO\n" + state.getMessage() + "\n" + state.getProgress() + "%\n[ ABRIR ANÁLISE ]"); else activeJob.setText(""); });
        vm.getModelStatusText().observe(getViewLifecycleOwner(), value -> updateWhisper.run());
        vm.getGeneralStatusText().observe(getViewLifecycleOwner(), value -> updateWhisper.run());
        updateWhisper.run();
        return root;
    }

    private boolean containsError(String value) {
        if (value == null) return false;
        String normalized = value.toLowerCase(java.util.Locale.ROOT);
        return normalized.contains("erro") || normalized.contains("error") || normalized.contains("falha");
    }

    private LinearLayout column() { LinearLayout l = new LinearLayout(requireContext()); l.setOrientation(LinearLayout.VERTICAL); l.setGravity(Gravity.TOP); return l; }
    private TextView text(String v, int s) { TextView t = new TextView(requireContext()); t.setText(v); t.setTextSize(s); t.setPadding(0, 10, 0, 10); return t; }
    private Button button(String v) { Button b = new Button(requireContext()); b.setText(v); b.setAllCaps(false); b.setMinHeight(58); return b; }
}
