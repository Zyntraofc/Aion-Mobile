package com.aula.aion.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.R;
import com.aula.aion.databinding.BottomSheetNotificacaoBinding;
import com.aula.aion.databinding.ItemNotificacaoBinding;
import com.aula.aion.databinding.ItemReclamacaoBinding;
import com.aula.aion.model.Notificacao;
import com.aula.aion.model.Reclamacao;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificacaoAdapter extends  RecyclerView.Adapter<NotificacaoAdapter.ViewHolder> {

    private List<Notificacao> notificacaos;
    private Activity activity;

    public NotificacaoAdapter(List<Notificacao> notificacaos, Activity activity) {
        this.activity = activity;
        this.notificacaos = notificacaos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemNotificacaoBinding binding;
        public ViewHolder(ItemNotificacaoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Notificacao notificacao) {
            binding.txtRemetente.setText(notificacao.getRemetente());
            binding.txtConteudo.setText(notificacao.getConteudo());
            // Calcular a diferença de tempo em milissegundos
            long now = System.currentTimeMillis();
            long notificationTime = notificacao.getData().getTime();
            long diffInMillis = now - notificationTime;

            // Converter para horas
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);

            if (diffInHours < 24) {
                // Menos de 1 dia: mostrar em horas (ex.: "5h")
                long hours = diffInHours;
                binding.txtTempo.setText(hours + "h");
            } else if (diffInHours < 24 * 7) {
                // De 1 a 6 dias: mostrar em dias (ex.: "2d")
                long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
                binding.txtTempo.setText(days + "d");
            } else {
                // 7 dias ou mais: mostrar em semanas (ex.: "1w")
                long weeks = diffInMillis / (7 * 24 * 60 * 60 * 1000);
                binding.txtTempo.setText(weeks + "w");
            }
        }
    }

    @NonNull
    @Override
    public NotificacaoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemNotificacaoBinding binding = ItemNotificacaoBinding.inflate(inflater, parent, false);
        return new NotificacaoAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacaoAdapter.ViewHolder holder, int position) {
        Notificacao notificacao = notificacaos.get(position); // Obtém o objeto Notificacao
        holder.bind(notificacao);
        holder.itemView.setOnClickListener(v -> {
            BottomSheetNotificacaoBinding binding = BottomSheetNotificacaoBinding.inflate(LayoutInflater.from(v.getContext()));
            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Primeiro, configura o feature

            dialog.setContentView(binding.getRoot());

            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setWindowAnimations(R.style.DialogAnimation);
                WindowManager.LayoutParams params = window.getAttributes();
                params.gravity = Gravity.BOTTOM;
                window.setAttributes(params);
            }

            // Preencher os dados
            binding.txtNmRemetente.setText(notificacao.getRemetente() != null ? notificacao.getRemetente() : "Desconhecido");
            binding.txtConteudoCompleto.setText(notificacao.getConteudo() != null && !notificacao.getConteudo().isBlank() ? notificacao.getConteudo() : "Sem conteúdo");
            binding.txtTempo2.setText(notificacao.getData() != null ? new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(notificacao.getData()) : "N/A");

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return notificacaos.size();
    }

    public void updateList(List<Notificacao> novaLista) {
        this.notificacaos = novaLista;
        notifyDataSetChanged(); // substituir futuramente por DiffUtil para maior performance
    }
}
