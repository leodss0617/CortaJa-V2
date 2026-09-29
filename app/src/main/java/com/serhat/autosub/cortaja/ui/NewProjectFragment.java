package com.serhat.autosub.cortaja.ui;

import android.net.Uri; import android.view.View; import android.view.ViewGroup; import android.widget.ArrayAdapter; import android.widget.Button; import android.widget.EditText; import android.widget.LinearLayout; import android.widget.Spinner; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.annotation.Nullable; import androidx.fragment.app.Fragment;
import com.serhat.autosub.ui.main.MainActivity;

public class NewProjectFragment extends Fragment {
    private Uri selectedUri; private EditText link; private TextView selected;
    @Nullable @Override public View onCreateView(@NonNull android.view.LayoutInflater i,@Nullable ViewGroup c,@Nullable android.os.Bundle b){LinearLayout root=new LinearLayout(requireContext());root.setOrientation(LinearLayout.VERTICAL);root.setPadding(24,18,24,12);
        TextView title=new TextView(requireContext());title.setText("Novo projeto\nOPÇÕES DE CORTE");title.setTextSize(24);root.addView(title);
        link=new EditText(requireContext());link.setHint("Cole o link do vídeo, VOD ou live");link.setSingleLine(true);root.addView(link);
        Button local=new Button(requireContext());local.setText("Ou escolher arquivo local");local.setOnClickListener(v->((MainActivity)requireActivity()).pickLocalVideo(this::setVideo));root.addView(local);
        selected=new TextView(requireContext());selected.setText("Nenhuma fonte selecionada");root.addView(selected);
        root.addView(label("Quantidade: Automático"));root.addView(label("Duração: Adaptativa"));root.addView(label("Objetivo: Melhores momentos"));root.addView(label("Formato: 9:16"));root.addView(label("Qualidade: 1080p"));root.addView(label("Legendas: Ligadas"));
        Button analyze=new Button(requireContext());analyze.setText("ANALISAR E GERAR CORTES");analyze.setOnClickListener(v->analyze());root.addView(analyze);return root;}
    private TextView label(String s){TextView t=new TextView(requireContext());t.setText(s);t.setPadding(0,8,0,2);return t;}
    private void setVideo(Uri u){selectedUri=u;selected.setText(u==null?"Nenhuma fonte selecionada":"Arquivo local selecionado: "+u.getLastPathSegment());}
    private void analyze(){if(selectedUri!=null){((MainActivity)requireActivity()).startCortaJaAnalysis(selectedUri);return;}String value=link.getText().toString().trim();if(value.isEmpty()){link.setError("Cole um link público");return;}((MainActivity)requireActivity()).startCortaJaLinkAnalysis(value);}
}
