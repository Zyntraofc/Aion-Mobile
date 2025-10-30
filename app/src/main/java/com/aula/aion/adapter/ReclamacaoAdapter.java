package com.aula.aion.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.R;
import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.databinding.BottomSheetNotificacaoBinding;
import com.aula.aion.databinding.ItemReclamacaoBinding;
import com.aula.aion.model.Notificacao;
import com.aula.aion.model.TpReclamacao;
import com.aula.aion.model.Reclamacao;

import java.time.format.DateTimeFormatter;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReclamacaoAdapter extends RecyclerView.Adapter<ReclamacaoAdapter.ViewHolder> {
    private List<Reclamacao> reclamacaos;
    private Retrofit retrofit;
    private Activity activity;


    public ReclamacaoAdapter(List<Reclamacao> reclamacaos, Activity activity) {
        this.reclamacaos = reclamacaos;
        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemReclamacaoBinding binding;

        public ViewHolder(ItemReclamacaoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Reclamacao reclamacao) {
            buscarTpReclamacao(reclamacao.getCdTpReclamacao(), binding);
            binding.dataTextView.setText(reclamacao.getReclamacao().format(DateTimeFormatter.ofPattern("dd/MM")));
            setarStatus(reclamacao.getStatus(), binding);
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
        Reclamacao reclamacao = reclamacaos.get(position);
        holder.bind(reclamacao);
        holder.itemView.setOnClickListener(v -> {
            BottomSheetNotificacaoBinding binding = BottomSheetNotificacaoBinding.inflate(LayoutInflater.from(v.getContext()));
            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setContentView(binding.getRoot());

            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                window.setWindowAnimations(R.style.DialogAnimation);
                WindowManager.LayoutParams params = window.getAttributes();
                params.gravity = Gravity.BOTTOM;
                window.setAttributes(params);
            }
            String resposta = null;
            // Preencher os dados
            binding.txtNmRemetente.setText("RH");
            if (reclamacao.getResposta() != null && !reclamacao.getStatus().equals("C"))
                resposta = reclamacao.getResposta();
            else if (reclamacao.getStatus().equals("E"))
                resposta = "Sua reclamação foi visualizada pelo RH.";
            else if (reclamacao.getStatus().equals("A"))
                resposta = "O RH ainda não enviou uma resposta para essa reclamação.";
            binding.txtConteudoCompleto.setText(resposta);

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return reclamacaos.size();
    }

    public void updateList(List<Reclamacao> novaLista) {
        this.reclamacaos = novaLista;
        notifyDataSetChanged(); 
    }
    private void buscarTpReclamacao(Long cdTpReclamacao, ItemReclamacaoBinding binding) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("colaborador", "colaboradorpass");
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", credentials)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        String url = "https://ms-aion-jpa.onrender.com";
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);

        serviceAPI_SQL.selecionarTpReclamacaoPorId(cdTpReclamacao).enqueue(new Callback<TpReclamacao>() {
            @Override
            public void onResponse(Call<TpReclamacao> call, Response<TpReclamacao> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Atualiza o texto do item específico
                    binding.tituloTextView.setText(response.body().getNome());
                }
            }

            @Override
            public void onFailure(Call<TpReclamacao> call, Throwable t) {
                Log.e("API_TpReclamacao", "Erro ao buscar endereço: " + t.getMessage());
            }
        });

    }
    private void setarStatus(String status, ItemReclamacaoBinding binding) {
        if (status.equals("A")) {
            binding.txtStatus.setText("Não respondido");
            binding.imgStatus.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_naorespondido));
        } else if (status.equals("E")) {
            binding.txtStatus.setText("Visto");
            binding.imgStatus.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_vista));
        } else if (status.equals("C")) {
            binding.txtStatus.setText("Respondido");
            binding.imgStatus.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_concluida));
        }
    }
}
