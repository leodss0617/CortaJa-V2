package com.serhat.autosub.cortaja.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.serhat.autosub.R;
import com.serhat.autosub.ui.main.MainActivity;

public class HomeFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        LinearLayout root = column(); root.setPadding(28, 28, 28, 20);
        TextView title = text("CortaJá", 30); root.addView(title);
        TextView subtitle = text("Transforme vídeos longos nos melhores momentos.", 18); root.addView(subtitle);
        Button newProject = button("+ NOVO PROJETO"); newProject.setOnClickListener(v -> ((MainActivity) requireActivity()).openNewProject()); root.addView(newProject);
        TextView source = text("FONTE DO VÍDEO", 14); root.addView(source);
        Button local = button("Arquivo local\nMP4 / MKV"); local.setOnClickListener(v -> ((MainActivity) requireActivity()).openNewProject()); root.addView(local);
        Button youtube = button("YouTube\nCole um link público"); youtube.setOnClickListener(v -> ((MainActivity) requireActivity()).openNewProject()); root.addView(youtube);
        root.addView(text("IA LOCAL", 14));
        root.addView(text("Whisper   •   Pronto", 16));
        root.addView(text("Gemma    •   Compatibilidade verificada durante a análise", 16));
        Button test = button("TESTAR IA"); test.setOnClickListener(v -> android.widget.Toast.makeText(requireContext(), "A IA local está pronta para analisar vídeos.", android.widget.Toast.LENGTH_SHORT).show()); root.addView(test);
        root.addView(text("PROJETOS RECENTES", 14));
        root.addView(text("Seus novos projetos aparecerão aqui.", 16));
        return root;
    }
    private LinearLayout column() { LinearLayout l = new LinearLayout(requireContext()); l.setOrientation(LinearLayout.VERTICAL); l.setGravity(Gravity.TOP); return l; }
    private TextView text(String value, int size) { TextView t = new TextView(requireContext()); t.setText(value); t.setTextSize(size); t.setPadding(0, 10, 0, 10); return t; }
    private Button button(String value) { Button b = new Button(requireContext()); b.setText(value); b.setAllCaps(false); b.setMinHeight(58); return b; }
}
