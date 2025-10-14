package com.aula.aion.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.databinding.ItemReclamacaoBinding;
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


    public ReclamacaoAdapter(List<Reclamacao> reclamacaos) {
        this.reclamacaos = reclamacaos;
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
        notifyDataSetChanged(); 
    }
    private void buscarTpReclamacao(Long cdTpReclamacao, ItemReclamacaoBinding binding) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("admin", "123456");
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
}
