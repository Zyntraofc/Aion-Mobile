package com.aula.aion.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aula.aion.adapter.ReclamacaoAdapter;
import com.aula.aion.databinding.FragmentReclamacaoBinding;


import com.aula.aion.R;
import com.aula.aion.model.Reclamacao;

import java.util.ArrayList;
import java.util.List;

public class ReclamacaoFragment extends Fragment {

    FragmentReclamacaoBinding binding;

    public ReclamacaoFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReclamacaoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        RecyclerView recyclerView = binding.reclamacaoRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Reclamacao> lista = new ArrayList<>();
        lista.add(new Reclamacao("Ambiente de Trabalho", "bdeyuvdfouevaovdfoa","18/07/2025"));
        lista.add(new Reclamacao("Abuso Sexual", "bdeyuvdfouevaovdfoa","18/07/2025"));

        ReclamacaoAdapter adapter = new ReclamacaoAdapter(lista);
        recyclerView.setAdapter(adapter);

        binding.btnReclamar.setOnClickListener(view1 -> {
            BottomSheetReclamacaoFragment bottomSheet = new BottomSheetReclamacaoFragment();
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });

        return view;
    }

    //Limpa quando a instancia morre
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}