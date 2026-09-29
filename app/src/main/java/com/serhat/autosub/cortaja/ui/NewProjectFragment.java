package com.serhat.autosub.cortaja.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.serhat.autosub.cortaja.CortaJaDomain;
import com.serhat.autosub.ui.main.MainActivity;

public class NewProjectFragment extends Fragment {
    private Uri selectedUri;
    private EditText youtube;
    private TextView selected;
    @Nullable @Override public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        LinearLayout root = new LinearLayout(requireContext()); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(24, 18, 24, 12);
        TextView title = new TextView(requireContext()); title.setText("Novo projeto\nCONFIGURAÇÕES DO CORTE"); title.setTextSize(24); root.addView(title);
        Button file = new Button(requireContext()); file.setText("Arquivo local"); file.setOnClickListener(v -> ((MainActivity) requireActivity()).pickLocalVideo(this::setVideo)); root.addView(file);
        selected = new TextView(requireContext()); selected.setText("Nenhum arquivo selecionado"); root.addView(selected);
        youtube = new EditText(requireContext()); youtube.setHint("Cole o link do YouTube"); youtube.setSingleLine(true); root.addView(youtube);
        root.addView(label("Quantidade")); root.addView(spinner(new String[]{"Automático", "3", "5", "10", "20"}));
        root.addView(label("Duração")); root.addView(spinner(new String[]{"Adaptativa", "15–30s", "30–60s", "60–90s"}));
        root.addView(label("Objetivo")); root.addView(spinner(new String[]{"Melhores momentos", "Viralidade", "Humor", "Informação", "Emoção"}));
        root.addView(label("Formato")); root.addView(spinner(new String[]{"9:16", "1:1", "16:9"}));
        root.addView(label("Qualidade")); root.addView(spinner(new String[]{"720p", "1080p"}));
        Button analyze = new Button(requireContext()); analyze.setText("ANALISAR VÍDEO"); analyze.setOnClickListener(v -> analyze()); root.addView(analyze);
        return root;
    }
    private TextView label(String s) { TextView t = new TextView(requireContext()); t.setText(s); t.setPadding(0, 8, 0, 2); return t; }
    private Spinner spinner(String[] values) { Spinner s = new Spinner(requireContext()); s.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, values)); return s; }
    private void setVideo(Uri uri) { selectedUri = uri; selected.setText(uri == null ? "Nenhum arquivo selecionado" : "Arquivo selecionado: " + uri.getLastPathSegment()); }
    private void analyze() {
        String source = selectedUri == null ? youtube.getText().toString().trim() : selectedUri.toString();
        if (selectedUri == null && !CortaJaDomain.isValidYoutubeUrl(source)) { youtube.setError("Cole um link público válido do YouTube"); return; }
        if (selectedUri == null) { android.widget.Toast.makeText(requireContext(), "O link foi aceito. Baixe o vídeo para analisar localmente.", android.widget.Toast.LENGTH_LONG).show(); return; }
        ((MainActivity) requireActivity()).startCortaJaAnalysis(selectedUri);
    }
}
