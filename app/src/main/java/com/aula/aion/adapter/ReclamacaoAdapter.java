package com.aula.aion.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.databinding.ItemReclamacaoBinding;
import com.aula.aion.databinding.ItemReclamacaoBinding;
import com.aula.aion.model.Reclamacao;
import com.aula.aion.model.Reclamacao;
import com.aula.aion.model.Reclamacao;

import java.util.List;

public class ReclamacaoAdapter extends RecyclerView.Adapter<ReclamacaoAdapter.ViewHolder> {
    private List<Reclamacao> reclamacaos;

    public ReclamacaoAdapter(List<Reclamacao> reclamacaos) {
        this.reclamacaos = reclamacaos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemReclamacaoBinding binding;

        public ViewHolder(ItemReclamacaoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Reclamacao Reclamacao) {
            binding.tituloTextView.setText(Reclamacao.getClasssificacao());
            binding.dataTextView.setText(Reclamacao.getData());
        }
    }

    @NonNull
    @Override
    public ReclamacaoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemReclamacaoBinding binding = ItemReclamacaoBinding.inflate(inflater, parent, false);
        return new ReclamacaoAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReclamacaoAdapter.ViewHolder holder, int position) {
        holder.bind(reclamacaos.get(position));
    }

    @Override
    public int getItemCount() {
        return reclamacaos.size();
    }

    public void updateList(List<Reclamacao> novaLista) {
        this.reclamacaos = novaLista;
        notifyDataSetChanged(); // substituir futuramente por DiffUtil para maior performance
    }
}
