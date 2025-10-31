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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aula.aion.Inicio;
import com.aula.aion.R;
import com.aula.aion.adapter.ReclamacaoAdapter;
import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.model.Funcionario;
import com.aula.aion.model.Reclamacao;
import com.aula.aion.sinal.EnviaSinalMethod;
import com.google.android.material.button.MaterialButton;
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

    private Long lCdFuncionario;
    private ReclamacaoAdapter adapter;

    // Views
    private MaterialButton btnReclamar;
    private TextView txtVista;
    private TextView txtRespondidas;
    private TextView txtProgressoVistas;
    private TextView txtProgressoRespondidas;
    private ProgressBar progressVistas;
    private ProgressBar progressRespondidas;
    private RecyclerView reclamacaoRecyclerView;
    private ProgressBar progressBar;

    public ReclamacaoFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reclamacao, container, false);

        btnReclamar = view.findViewById(R.id.btn_reclamar);
        txtVista = view.findViewById(R.id.txt_vista);
        txtRespondidas = view.findViewById(R.id.txt_respondidas);
        txtProgressoVistas = view.findViewById(R.id.txt_progresso_vistas);
        txtProgressoRespondidas = view.findViewById(R.id.txt_progresso_respondidas);
        progressVistas = view.findViewById(R.id.progress_vistas);
        progressRespondidas = view.findViewById(R.id.progress_respondidas);
        reclamacaoRecyclerView = view.findViewById(R.id.reclamacaoRecyclerView);
        progressBar = view.findViewById(R.id.progress);

        reclamacaoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Inicio activity = (Inicio) getActivity();
        adapter = new ReclamacaoAdapter(new ArrayList<>(), activity);
        reclamacaoRecyclerView.setAdapter(adapter);

        if (activity != null) {
            Funcionario funcionario = activity.getFuncionario();
            if (funcionario != null) {
                lCdFuncionario = funcionario.getCdMatricula();
                getReclamacaoByUser(funcionario.getCdMatricula());
            }
        }

        EnviaSinalMethod enviaSinalMethod = new EnviaSinalMethod();
        enviaSinalMethod.enviaSinal(lCdFuncionario);

        btnReclamar.setOnClickListener(view1 -> {
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
        // Mostrar loading
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("colaborador", "colaboradorpass");
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
                // Esconder loading
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Reclamacao> reclamacoes = response.body();
                    Log.d("API", "Reclamações recebidas: " + reclamacoes.size());

                    processaListaReclamacoes(reclamacoes);
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
                // Esconder loading
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

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

    private void processaListaReclamacoes(List<Reclamacao> reclamacoes) {
        int respondidas = 0;
        int vistas = 0;
        int size = reclamacoes.size();

        for (Reclamacao reclamacao : reclamacoes) {
            if (reclamacao.getStatus().equals("C")) respondidas++;
            if (reclamacao.getStatus().equals("E")) vistas++;
        }

        txtVista.setText(String.valueOf(vistas));
        txtRespondidas.setText(String.valueOf(respondidas));

        int percentualVistas = size > 0 ? (int) ((vistas / (float) size) * 100) : 0;
        int percentualRespondidas = size > 0 ? (int) ((respondidas / (float) size) * 100) : 0;

        progressRespondidas.setProgress(percentualRespondidas);
        progressVistas.setProgress(percentualVistas);

        txtProgressoRespondidas.setText(respondidas + "/" + size);
        txtProgressoVistas.setText(vistas + "/" + size);

        adapter.updateList(reclamacoes);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpar referências das views
        btnReclamar = null;
        txtVista = null;
        txtRespondidas = null;
        txtProgressoVistas = null;
        txtProgressoRespondidas = null;
        progressVistas = null;
        progressRespondidas = null;
        reclamacaoRecyclerView = null;
        progressBar = null;
    }
}