package com.aula.aion.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aula.aion.Inicio;
import com.aula.aion.adapter.ReclamacaoAdapter;
import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.databinding.FragmentReclamacaoBinding;

import com.aula.aion.model.Funcionario;
import com.aula.aion.model.Reclamacao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReclamacaoFragment extends Fragment {

    FragmentReclamacaoBinding binding;
    private Long lCdFuncionario;
    private ReclamacaoAdapter adapter;

    public ReclamacaoFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // ✅ Usa somente o binding (sem inflar outro layout)
        binding = FragmentReclamacaoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // ✅ Configura o RecyclerView corretamente
        RecyclerView recyclerView = binding.reclamacaoRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReclamacaoAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // ✅ Recupera o funcionário da Activity, se existir
        Inicio activity = (Inicio) getActivity();
        if (activity != null) {
            Funcionario funcionario = activity.getFuncionario();
            if (funcionario != null) {
                lCdFuncionario = funcionario.getCdMatricula();
                getReclamacaoByUser(funcionario.getCdMatricula());
            }
        }

        // ✅ Botão de reclamação — mantido igual ao seu
        binding.btnReclamar.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putLong("lCdFuncionario", lCdFuncionario);

            BottomSheetReclamacaoFragment bottomSheet = new BottomSheetReclamacaoFragment();
            bottomSheet.setArguments(bundle);

            bottomSheet.setOnDismissListener(dialogInterface -> {
                getReclamacaoByUser(lCdFuncionario);
            });

            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });

        return view;
    }


    private void getReclamacaoByUser(Long id) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("admin", "123456");
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", credentials)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, type, context) ->
                                LocalDate.parse(json.getAsString())
                )
                .create();

        String url = "https://ms-aion-jpa.onrender.com";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);

        Call<List<Reclamacao>> call = serviceAPI_SQL.selecionarReclamacaoPorFuncionario(id);

        call.enqueue(new Callback<List<Reclamacao>>() {
            @Override
            public void onResponse(Call<List<Reclamacao>> call, Response<List<Reclamacao>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reclamacao> reclamacoes = response.body();
                    Log.d("API", "Reclamações recebidas: " + reclamacoes.size());

                    // Atualizar apenas a lista do adapter existente
                    adapter.updateList(reclamacoes);
                } else {
                    Log.e("API", "Erro na resposta: " + response.code());

                    // Tentar obter detalhes do erro
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("API", "Corpo do erro: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("API", "Erro ao ler corpo da resposta: " + e.getMessage());
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(),
                                        "Erro ao carregar reclamações: " + response.code(),
                                        Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Reclamacao>> call, Throwable t) {
                Log.e("API", "Erro na chamada: " + t.getMessage(), t);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(),
                                    "Erro de conexão: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}