package com.aula.aion.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.databinding.ItemJustificativaBinding;
import com.aula.aion.model.Justificativa;
import com.aula.aion.ui.home.BottomSheetJustificativaFragment;

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
            binding.tituloTextView.setText(justificativa.getTitulo());
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
        Justificativa justificativa = justificativas.get(position); // Obtém o objeto Justificativa
        holder.bind(justificativa);

        holder.itemView.setOnClickListener(v -> {
            BottomSheetJustificativaFragment bottomSheet = BottomSheetJustificativaFragment.newInstance();

            // Passar dados para o BottomSheet
            Bundle args = new Bundle();
            args.putString("data", justificativa.getData()); // Usa o método getData() da Justificativa
            args.putString("titulo", justificativa.getTitulo()); // Opcional: passa o título
            bottomSheet.setArguments(args);

            // Exibir o BottomSheet com verificação de contexto
            if (v.getContext() instanceof FragmentActivity) {
                bottomSheet.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "BottomSheetJustificativa");
            } else {
                // Lidar com caso em que o contexto não é uma FragmentActivity (opcional)
                throw new IllegalStateException("Contexto não é uma FragmentActivity");
            }
        });
    }

    @Override
    public int getItemCount() {
        return justificativas.size();
    }

    // Atualização segura da lista
    public void updateList(List<Justificativa> novaLista) {
        this.justificativas = novaLista;
        notifyDataSetChanged(); // Substituir futuramente por DiffUtil para maior performance
    }
}