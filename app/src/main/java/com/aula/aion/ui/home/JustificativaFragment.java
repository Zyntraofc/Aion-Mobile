package com.aula.aion.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.Inicio;
import com.aula.aion.R;
import com.aula.aion.adapter.JustificativaAdapter;
import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.model.CountResponse;
import com.aula.aion.model.Funcionario;
import com.aula.aion.model.Justificativa;
import com.aula.aion.model.RelatorioPresenca;
import com.aula.aion.sinal.EnviaSinalMethod;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
    private Retrofit retrofit;
    private int totalFaltas = 0;
    private int countJustificadas = 0;
    private int diasUteisNoMes = 0;
    private int countPendentes = 0;

    // Views
    private TextView txtFaltas;
    private TextView txtProgressoFaltas;
    private ProgressBar progressFaltas;
    private TextView txtJustificadas;
    private TextView txtProgressoJustificada;
    private ProgressBar progressJustificadas;
    private RecyclerView justificativaRecyclerView;

    public JustificativaFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_justificativa, container, false);

        // Inicializar as views
        txtFaltas = view.findViewById(R.id.txt_faltas);
        txtProgressoFaltas = view.findViewById(R.id.txt_progresso_faltas);
        progressFaltas = view.findViewById(R.id.progress_faltas);
        txtJustificadas = view.findViewById(R.id.txt_justificadas);
        txtProgressoJustificada = view.findViewById(R.id.txt_progresso_justificada);
        progressJustificadas = view.findViewById(R.id.progress_justificadas);
        justificativaRecyclerView = view.findViewById(R.id.justificativaRecyclerView);

        Inicio activity = (Inicio) getActivity();
        if (activity != null) {
            Funcionario funcionario = activity.getFuncionario();

            calcularDiasUteisDoMes();

            getRelatorioPresencas(funcionario.getCdMatricula(), view);

            EnviaSinalMethod enviaSinalMethod = new EnviaSinalMethod();
            enviaSinalMethod.enviaSinal(funcionario.getCdMatricula());
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean fromCalendar = getArguments() != null && getArguments().getBoolean("fromCalendar", false);
        if (fromCalendar) {
            Inicio activity = (Inicio) getActivity();
            if (activity != null) {
                BottomNavigationView navView = activity.findViewById(R.id.nav_view);

                navView.setOnItemSelectedListener(item -> {
                    NavController controller = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
                    if (item.getItemId() == R.id.nav_home) {
                        controller.popBackStack(R.id.nav_home, false);
                    } else {
                        // Mantém navegação normal para outros itens
                        NavigationUI.onNavDestinationSelected(item, controller);
                    }
                    return true;
                });
            }
        }
    }



    private void calcularDiasUteisDoMes() {
        YearMonth mesAtual = YearMonth.now();
        int diasNoMes = mesAtual.lengthOfMonth();
        diasUteisNoMes = 0;

        for (int dia = 1; dia <= diasNoMes; dia++) {
            LocalDate data = mesAtual.atDay(dia);
            // Contar apenas dias úteis (segunda a sexta)
            if (data.getDayOfWeek().getValue() >= 1 && data.getDayOfWeek().getValue() <= 5) {
                diasUteisNoMes++;
            }
        }

        Log.d("API", "Dias úteis no mês: " + diasUteisNoMes);
    }

    private void getRelatorioPresencas(Long id, View view) {
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
                    countJustificadas(id);
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
        List<Justificativa> lista = new ArrayList<>();

        for (RelatorioPresenca relatorio : relatorioPresenca) {
            switch (relatorio.getStatusDia()) {
                case 1:
                    finalSemana++;
                    break;
                case 2:
                    presenca++;
                    break;
                case 3:
                    parcial++;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
                    String dataFormatada = relatorio.getDataDia().format(formatter);
                    lista.add(new Justificativa(dataFormatada, 1));
                    break;
                case 4:
                    ausente++;
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM");
                    String data = relatorio.getDataDia().format(format);
                    lista.add(new Justificativa(data, 0));
                    break;
            }
        }

        totalFaltas = ausente + parcial;

        justificativaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        countPendentes = lista.size();
        JustificativaAdapter adapter = new JustificativaAdapter(lista);
        justificativaRecyclerView.setAdapter(adapter);
        ProgressBar progressBar = view.findViewById(R.id.progressJustificativa);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setEnabled(false);

        atualizarUIFaltas(totalFaltas);
    }

    private void atualizarUIFaltas(int totalFaltas) {
        txtFaltas.setText(String.valueOf(totalFaltas));
        txtProgressoFaltas.setText(totalFaltas + "/" + diasUteisNoMes);

        // Calcular porcentagem de faltas
        int progressFaltasPercent = diasUteisNoMes > 0 ? (int) ((totalFaltas / (float) diasUteisNoMes) * 100) : 0;
        progressFaltas.setProgress(progressFaltasPercent);

        Log.d("API", "Faltas: " + totalFaltas + "/" + diasUteisNoMes + " = " + progressFaltasPercent + "%");
    }

    private void atualizarUIJustificadas(int count) {
        countJustificadas = count;
        txtJustificadas.setText(String.valueOf(countJustificadas));
        txtProgressoJustificada.setText(countJustificadas + "/" + (countPendentes + countJustificadas));

        // Calcular porcentagem de justificativas em relação aos pendentes
        int progressJustificadasPercent = (countPendentes + countJustificadas) > 0 ?
                (int) ((countJustificadas / (float) (countPendentes + countJustificadas)) * 100) : 0;
        progressJustificadas.setProgress(progressJustificadasPercent);

        Log.d("API", "Justificadas: " + countJustificadas + "/" + (countPendentes + countJustificadas) + " = " + progressJustificadasPercent + "%");
    }

    private void countJustificadas(Long id) {
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

        serviceAPI_SQL.countJustificativa(id).enqueue(new Callback<CountResponse>() {
            @Override
            public void onResponse(Call<CountResponse> call, Response<CountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().getCount();
                    Log.d("API", "Quantidade de justificativas: " + count);
                    atualizarUIJustificadas(count);
                }
            }

            @Override
            public void onFailure(Call<CountResponse> call, Throwable t) {
                Log.e("API", "Erro ao buscar justificativas: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpar referências das views
        txtFaltas = null;
        txtProgressoFaltas = null;
        progressFaltas = null;
        txtJustificadas = null;
        txtProgressoJustificada = null;
        progressJustificadas = null;
        justificativaRecyclerView = null;
        Inicio activity = (Inicio) getActivity();
        if (activity != null) {
            BottomNavigationView navView = activity.findViewById(R.id.nav_view);
            NavController controller = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupWithNavController(navView, controller);
        }
    }
}