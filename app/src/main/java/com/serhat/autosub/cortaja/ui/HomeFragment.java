package com.serhat.autosub.cortaja.ui;

import android.view.Gravity; import android.view.View; import android.view.ViewGroup; import android.widget.Button; import android.widget.EditText; import android.widget.LinearLayout; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.annotation.Nullable; import androidx.fragment.app.Fragment;
import com.serhat.autosub.ui.main.MainActivity;

public class HomeFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull android.view.LayoutInflater i,@Nullable ViewGroup c,@Nullable android.os.Bundle b){
        LinearLayout root=column();root.setPadding(28,28,28,20);TextView title=text("CortaJá",30);root.addView(title);root.addView(text("Transforme vídeos longos nos melhores momentos.",18));
        EditText link=new EditText(requireContext());link.setHint("Cole o link do vídeo, VOD ou live");link.setSingleLine(true);root.addView(link);
        Button analyze=button("ANALISAR E GERAR CORTES");analyze.setOnClickListener(v->{String value=link.getText().toString().trim();if(value.isEmpty()){link.setError("Cole um link público");return;}((MainActivity)requireActivity()).startCortaJaLinkAnalysis(value);});root.addView(analyze);
        Button newProject=button("+ NOVO PROJETO");newProject.setOnClickListener(v->((MainActivity)requireActivity()).openNewProject());root.addView(newProject);
        root.addView(text("IA LOCAL",14));root.addView(text("Whisper  •  Pronto",16));root.addView(text("Gemma  •  será usada quando compatível; fallback local disponível",16));root.addView(text("PROJETOS RECENTES",14));root.addView(text("Seus projetos aparecerão aqui.",16));return root;
    }
    private LinearLayout column(){LinearLayout l=new LinearLayout(requireContext());l.setOrientation(LinearLayout.VERTICAL);l.setGravity(Gravity.TOP);return l;}
    private TextView text(String v,int s){TextView t=new TextView(requireContext());t.setText(v);t.setTextSize(s);t.setPadding(0,10,0,10);return t;}
    private Button button(String v){Button b=new Button(requireContext());b.setText(v);b.setAllCaps(false);b.setMinHeight(58);return b;}
}
