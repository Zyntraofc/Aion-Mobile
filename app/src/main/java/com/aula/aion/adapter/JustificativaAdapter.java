package com.aula.aion.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.databinding.ItemJustificativaBinding;
import com.aula.aion.model.Justificativa;

import java.util.List;

public class JustificativaAdapter extends RecyclerView.Adapter<JustificativaAdapter.ViewHolder> {

    private List<Justificativa> justificativas;

    public JustificativaAdapter(List<Justificativa> justificativas) {
        this.justificativas = justificativas;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemJustificativaBinding binding;

        public ViewHolder(ItemJustificativaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Justificativa justificativa) {
            binding.tituloTextView.setText(justificativa.getTitulo());//Analisar a necessidade de mais campos pós verificação da justificativa
            binding.dataTextView.setText(justificativa.getData());
        }
    }

    @NonNull
    @Override
    public JustificativaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemJustificativaBinding binding = ItemJustificativaBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JustificativaAdapter.ViewHolder holder, int position) {
        holder.bind(justificativas.get(position));
    }

    @Override
    public int getItemCount() {
        return justificativas.size();
    }

    // Atualização segura da lista
    public void updateList(List<Justificativa> novaLista) {
        this.justificativas = novaLista;
        notifyDataSetChanged(); // substituir futuramente por DiffUtil para maior performance
    }
}
