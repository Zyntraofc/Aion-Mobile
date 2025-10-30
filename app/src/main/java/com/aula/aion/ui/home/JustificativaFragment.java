package com.aula.aion.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.Inicio;
import com.aula.aion.R; // Make sure R.java is correctly imported
import com.aula.aion.adapter.JustificativaAdapter;
import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.databinding.FragmentJustificativaBinding;
import com.aula.aion.model.Funcionario;
import com.aula.aion.model.Justificativa;
import com.aula.aion.model.RelatorioPresenca;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
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

public class JustificativaFragment extends Fragment {
    private FragmentJustificativaBinding binding;

    public JustificativaFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentJustificativaBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Inicio activity = (Inicio) getActivity();
        if (activity != null) {
            Funcionario funcionario= activity.getFuncionario();
            getRelatorioPresencas(funcionario.getCdMatricula(), view);
        }

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
    private void getRelatorioPresencas(Long id, View view) {
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
                .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                    @Override
                    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                            throws JsonParseException {
                        return LocalDate.parse(json.getAsString());
                    }
                })
                .create();


        String url = "https://ms-aion-jpa.onrender.com";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);

        Call<List<RelatorioPresenca>> call = serviceAPI_SQL.listarRelatorioPresenca(id);

        call.enqueue(new Callback<List<RelatorioPresenca>>() {
            @Override
            public void onResponse(Call<List<RelatorioPresenca>> call, Response<List<RelatorioPresenca>> response) {
                Log.d("API", "Resposta da API: " + response.code());
                if (response.isSuccessful()) {
                    Log.d("API", "Sucesso na resposta: " + response.body());
                    List<RelatorioPresenca> relatorio = response.body();
                    processaRelatorioPresenca(relatorio, view);
                } else {
                    Log.e("API", "Erro na resposta: " + response.code());

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
                                        "Erro ao carregar relatório: " + response.code(),
                                        Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }

            @Override
            public void onFailure(Call<List<RelatorioPresenca>> call, Throwable t) {
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

    private void processaRelatorioPresenca(List<RelatorioPresenca> relatorioPresenca, View view) {
        Log.d("API", "processaRelatorioPresenca: " + relatorioPresenca.size());
        int presenca = 0;
        int ausente = 0;
        int parcial = 0;
        int finalSemana = 0;

        for (RelatorioPresenca relatorio : relatorioPresenca) {
            switch (relatorio.getStatusDia()) {
                case 2: presenca++; break;
                case 4: ausente++; break;
                case 3: parcial++; break;
                default: finalSemana++; break;
            }
        }
        TextView txtNumFalta = view.findViewById(R.id.txt_faltas);
        TextView txtJustificadas = view.findViewById(R.id.txt_justificadas);
        txtJustificadas.setText(String.valueOf(parcial));
        txtNumFalta.setText(String.valueOf(ausente));
    }
}