package com.aula.aion.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.R; // Make sure R.java is correctly imported
import com.aula.aion.adapter.JustificativaAdapter;
import com.aula.aion.databinding.FragmentJustificativaBinding;
import com.aula.aion.model.Justificativa;

import java.util.ArrayList;
import java.util.List;

public class JustificativaFragment extends Fragment {
    private FragmentJustificativaBinding binding;

    public JustificativaFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentJustificativaBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        RecyclerView recyclerView = binding.justificativaRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Justificativa> lista = new ArrayList<>();
        lista.add(new Justificativa("Justificar falta", "18/07"));
        lista.add(new Justificativa("Justificar falta",  "19/07"));

        JustificativaAdapter adapter = new JustificativaAdapter(lista);
        recyclerView.setAdapter(adapter);

        return view;

    }


    //Limpa quando a instancia morre
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}