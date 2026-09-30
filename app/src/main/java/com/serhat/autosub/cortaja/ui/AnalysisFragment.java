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
import com.serhat.autosub.service.AutoSubTaskState;
import com.serhat.autosub.ui.main.MainViewModel;

public class AnalysisFragment extends Fragment {
    @Nullable @Override public android.view.View onCreateView(@NonNull android.view.LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle b){
        LinearLayout root=new LinearLayout(requireContext());root.setOrientation(LinearLayout.VERTICAL);root.setPadding(28,28,28,20);
        TextView title=new TextView(requireContext());title.setText("Análise em andamento");title.setTextSize(24);root.addView(title);
        ProgressBar progress=new ProgressBar(requireContext(),null,android.R.attr.progressBarStyleHorizontal);progress.setMax(100);root.addView(progress,new LinearLayout.LayoutParams(-1,42));
        TextView details=new TextView(requireContext());details.setTextSize(16);root.addView(details);
        MainViewModel vm=new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        Runnable render=()->{AutoSubTaskState s=vm.getCurrentTaskState().getValue();String error=vm.getShortsError().getValue();if(error!=null&&!error.trim().isEmpty()){details.setText("✕ "+error);return;}if(s==null){details.setText("Preparando análise...");return;}progress.setIndeterminate(s.getProgress()<0);if(s.getProgress()>=0)progress.setProgress(s.getProgress());details.setText((s.getMessage()==null||s.getMessage().isEmpty()?"Processando":s.getMessage())+"\\n\\nProgresso total: "+(s.getProgress()<0?"calculando":s.getProgress()+"%")+"\\n\\nÚltima atividade: agora");};
        vm.getCurrentTaskState().observe(getViewLifecycleOwner(),v->render.run());vm.getShortsError().observe(getViewLifecycleOwner(),v->render.run());vm.getGeneralStatusText().observe(getViewLifecycleOwner(),v->render.run());render.run();return root;
    }
}
