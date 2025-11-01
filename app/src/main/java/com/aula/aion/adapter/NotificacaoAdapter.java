package com.aula.aion.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.api.ServiceAPI_NOSQL;
import com.aula.aion.databinding.BottomSheetNotificacaoBinding;
import com.aula.aion.databinding.ItemNotificacaoBinding;
import com.aula.aion.model.Notificacao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificacaoAdapter extends RecyclerView.Adapter<NotificacaoAdapter.ViewHolder> {

    private List<Notificacao> notificacoes;
    private Activity activity;
    private Retrofit retrofit;

    public NotificacaoAdapter(List<Notificacao> notificacaos, Activity activity) {
        this.activity = activity;
        this.notificacoes = notificacaos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemNotificacaoBinding binding;
        public ViewHolder(ItemNotificacaoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Notificacao notificacao) {
            binding.txtTitulo.setText(notificacao.getTitulo());
            binding.txtConteudo.setText(notificacao.getDescricao());
            String dataNotificacao = notificacao.getData();

            // Controla visibilidade do indicador de notificação não lida
            if ("F".equals(notificacao.getStatus())) {
                // Status "F" = já foi visualizada = esconde o indicador
                binding.view.setVisibility(View.INVISIBLE);
            } else {
                // Status "A" ou qualquer outro = não foi visualizada = mostra o indicador
                binding.view.setVisibility(View.VISIBLE);
            }

            try {
                // Converte String para LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                LocalDateTime localDateTime = LocalDateTime.parse(dataNotificacao, formatter);

                // Converte para ZonedDateTime
                ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
                long notificationTime = zonedDateTime.toInstant().toEpochMilli();

                long now = System.currentTimeMillis();
                long diffInMillis = now - notificationTime;

                long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);

                if (diffInHours < 24) {
                    // Menos de 1 dia: mostrar em horas (ex.: "5h")
                    binding.txtTempo.setText(diffInHours + "h");
                } else if (diffInHours < 24 * 7) {
                    // De 1 a 6 dias: mostrar em dias (ex.: "2d")
                    long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
                    binding.txtTempo.setText(days + "d");
                } else {
                    // 7 dias ou mais: mostrar em semanas (ex.: "1sem")
                    long weeks = diffInMillis / (7L * 24 * 60 * 60 * 1000);
                    binding.txtTempo.setText(weeks + "sem");
                }
            } catch (Exception e) {
                e.printStackTrace();
                binding.txtTempo.setText("--");
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
        Notificacao notificacao = notificacoes.get(position);
        holder.bind(notificacao);
        exibirNotificacao(holder, notificacao);
    }

    @Override
    public int getItemCount() {
        return notificacoes.size();
    }

    public void updateList(List<Notificacao> novaLista) {
        this.notificacoes = novaLista;
        notifyDataSetChanged();
    }

    private void exibirNotificacao(NotificacaoAdapter.ViewHolder holder, Notificacao notificacao) {
        holder.itemView.setOnClickListener(v -> {

            BottomSheetNotificacaoBinding binding = BottomSheetNotificacaoBinding.inflate(LayoutInflater.from(v.getContext()));
            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setContentView(binding.getRoot());

            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams params = window.getAttributes();
                params.gravity = Gravity.BOTTOM;
                window.setAttributes(params);
            }

            binding.txtNmRemetente.setText(notificacao.getTitulo());
            binding.txtConteudoCompleto.setText(notificacao.getDescricao());

            dialog.show();

            // Verifica se a notificação ainda não foi visualizada
            if (!"F".equals(notificacao.getStatus())) {
                visualizarNotificacao(notificacao.getCdNotificacao());
                notificacao.setStatus("F"); // Marca localmente como visualizada
                holder.binding.view.setVisibility(View.INVISIBLE); // Esconde o indicador
            }
        });
    }

    private void visualizarNotificacao(String cdNotificacao) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("colaborador", "colaboradorpass");
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", credentials)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        String url = "https://ms-aion-mongodb.onrender.com";
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceAPI_NOSQL serviceAPI_NOSQL = retrofit.create(ServiceAPI_NOSQL.class);
        serviceAPI_NOSQL.atualizarNotificacao(cdNotificacao, "F").enqueue(new Callback<Notificacao>() {
            @Override
            public void onResponse(Call<Notificacao> call, Response<Notificacao> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(activity, "Notificação visualizada.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Notificacao> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(activity, "Erro ao visualizar notificação.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}