package com.serhat.autosub.cortaja.ui;
import android.view.ViewGroup; import android.widget.LinearLayout; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.annotation.Nullable; import androidx.fragment.app.Fragment;
public class MyCutsFragment extends Fragment { @Nullable @Override public android.view.View onCreateView(@NonNull android.view.LayoutInflater i,@Nullable ViewGroup c,@Nullable android.os.Bundle b){ LinearLayout l=new LinearLayout(requireContext()); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(28,28,28,20); TextView t=new TextView(requireContext()); t.setText("Meus Cortes\n\nNenhum corte gerado ainda."); t.setTextSize(22); l.addView(t); return l; } }
