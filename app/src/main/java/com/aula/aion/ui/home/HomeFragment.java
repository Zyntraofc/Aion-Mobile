package com.aula.aion.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.Inicio;
import com.aula.aion.R;
import com.aula.aion.adapter.CalendarAdapter;
import com.aula.aion.api.ServiceAPI_NOSQL;
import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.databinding.ActivityInicioBinding;
import com.aula.aion.databinding.FragmentHomeBinding;
import com.aula.aion.model.EnviaSinal;
import com.aula.aion.model.Funcionario;
import com.aula.aion.model.RelatorioPresenca;
import com.aula.aion.sinal.EnviaSinalMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private Retrofit retrofit;
    private FirebaseAuth mAuth;
    // Componentes do calendário
    private TextView monthYearTextView;
    private RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;
    private Calendar currentCalendar;
    private FragmentHomeBinding binding;
    private List<RelatorioPresenca> relatorioPresencaList;
    private int diasUteisNoMes = 0;
    private Funcionario funcionario;
    // Construtor público vazio necessário
    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();

        // Calcular dias úteis do mês atual
        calcularDiasUteisDoMes();

        chamaAPI_GetByEmail(email, view);

        monthYearTextView = view.findViewById(R.id.monthYearTextView);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        ImageButton previousMonthButton = view.findViewById(R.id.previousMonthButton);
        ImageButton nextMonthButton = view.findViewById(R.id.nextMonthButton);

        currentCalendar = Calendar.getInstance();
        relatorioPresencaList = new ArrayList<>();

        previousMonthButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            setupCalendar();
        });

        nextMonthButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            setupCalendar();
        });
        return view;
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

    private void setupCalendar() {
        if (getContext() == null) return;

        monthYearTextView.setText(new SimpleDateFormat("MMMM 'de' yyyy", new Locale("pt", "BR"))
                .format(currentCalendar.getTime()));

        List<CalendarAdapter.CalendarDay> days = new ArrayList<>();
        Calendar monthCalendar = (Calendar) currentCalendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (firstDayOfMonth == -1) firstDayOfMonth = 6;
        if (firstDayOfMonth == 0) firstDayOfMonth = 7;

        // Adicionar dias do mês anterior
        Calendar prevMonthCalendar = (Calendar) monthCalendar.clone();
        prevMonthCalendar.add(Calendar.MONTH, -1);
        int daysInPrevMonth = prevMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = firstDayOfMonth - 1; i >= 0; i--) {
            int dayNum = daysInPrevMonth - i;
            prevMonthCalendar.set(Calendar.DAY_OF_MONTH, dayNum);
            LocalDate dataDia = LocalDate.of(
                    prevMonthCalendar.get(Calendar.YEAR),
                    prevMonthCalendar.get(Calendar.MONTH) + 1,
                    dayNum
            );
            CalendarAdapter.CalendarDay day = new CalendarAdapter.CalendarDay(
                    String.valueOf(dayNum), false, dataDia
            );
            days.add(day);
        }

        // Adicionar dias do mês atual
        int daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= daysInMonth; i++) {
            monthCalendar.set(Calendar.DAY_OF_MONTH, i);
            LocalDate dataDia = LocalDate.of(
                    monthCalendar.get(Calendar.YEAR),
                    monthCalendar.get(Calendar.MONTH) + 1,
                    i
            );
            CalendarAdapter.CalendarDay day = new CalendarAdapter.CalendarDay(
                    String.valueOf(i), true, dataDia
            );
            days.add(day);
        }

        // Adicionar dias do próximo mês
        int totalDays = days.size();
        int daysToAddNextMonth = 0;
        if (totalDays < 42) {
            daysToAddNextMonth = 42 - totalDays;
        }

        Calendar nextMonthCalendar = (Calendar) monthCalendar.clone();
        nextMonthCalendar.add(Calendar.MONTH, 1);
        nextMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        for (int i = 1; i <= daysToAddNextMonth; i++) {
            nextMonthCalendar.set(Calendar.DAY_OF_MONTH, i);
            LocalDate dataDia = LocalDate.of(
                    nextMonthCalendar.get(Calendar.YEAR),
                    nextMonthCalendar.get(Calendar.MONTH) + 1,
                    i
            );
            CalendarAdapter.CalendarDay day = new CalendarAdapter.CalendarDay(
                    String.valueOf(i), false, dataDia
            );
            days.add(day);
        }

        // Criar e configurar o adapter com a lista de relatórios
        calendarAdapter = new CalendarAdapter(getContext(), days, relatorioPresencaList);

        // Configurar o listener de cliques
        calendarAdapter.setOnDayClickListener(new CalendarAdapter.OnDayClickListener() {
            @Override
            public void onTodayClick() {
                // Abre o BottomSheet para o dia de hoje
                if (funcionario != null) {
                    BottomSheetBatidaFragment bottomSheet = new BottomSheetBatidaFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("funcionario", funcionario);
                    bottomSheet.setArguments(args);
                    bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
                } else {
                    Toast.makeText(getContext(), "Carregando dados do funcionário...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAbsentDayClick(LocalDate date, RelatorioPresenca presenca) {
                Bundle args = new Bundle();
                args.putSerializable("data", date);
                args.putSerializable("presenca", presenca);
                args.putBoolean("fromCalendar", true);

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.nav_justificativa, args);
            }

        });

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private void chamaAPI_GetByEmail(String email, View view) {
        Log.d("chamaAPI_GetByEmail", "Chamando API com email: " + email);

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

        serviceAPI_SQL.selecionarFuncionarioPorEmail(email).enqueue(new Callback<Funcionario>() {
            @Override
            public void onResponse(Call<Funcionario> call, Response<Funcionario> response) {
                if (response.isSuccessful()) {
                    Log.d("chamaAPI_GetByEmail", "Resposta da API: " + response);
                    Funcionario funcionarioRetorno = response.body();
                    if (funcionarioRetorno != null) {
                        // Armazenar o funcionário na variável de instância
                        funcionario = funcionarioRetorno;

                        TextView txtBemVindo = view.findViewById(R.id.txtBemVindo);
                        txtBemVindo.setText("Olá, " + funcionarioRetorno.getNomeCompleto());
                        verificaExisteNotificacao(funcionarioRetorno.getCdMatricula());
                        Log.d("VER DATA", funcionarioRetorno.getNascimento());
                        Inicio activity = (Inicio) getActivity();
                        if (activity != null) {
                            activity.setFuncionario(funcionarioRetorno);
                            EnviaSinalMethod enviaSinalMethod = new EnviaSinalMethod();
                            enviaSinalMethod.enviaSinal(funcionario.getCdMatricula());
                        }
                        getRelatorioPresencas(funcionarioRetorno.getCdMatricula(), view);
                    }
                }
            }

            @Override
            public void onFailure(Call<Funcionario> call, Throwable t) {
                t.printStackTrace();
                Log.d("chamaAPI_GetByEmail", "Erro na chamada da API: " + t.getMessage());
            }
        });
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

        // Armazenar a lista de relatórios para uso no calendário
        relatorioPresencaList = relatorioPresenca;

        int presenca = 0;
        int ausente = 0;
        int parcial = 0;
        int finalSemana = 0;

        for (RelatorioPresenca relatorio : relatorioPresenca) {
            switch (relatorio.getStatusDia()) {
                case 1: finalSemana++; break;
                case 2: presenca++; break;
                case 3: parcial++; break;
                case 4: ausente++; break;
            }
        }

        // Atualizar UI
        atualizarUIFaltas(ausente, view);
        atualizarUIPresencas(presenca, view);

        setupCalendar();
    }

    private void atualizarUIFaltas(int totalFaltas, View view) {
        TextView txtNumFalta = view.findViewById(R.id.txt_num_falta);
        TextView txtProgressoFalta = view.findViewById(R.id.txt_progresso_vistas);
        ProgressBar progressFaltas = view.findViewById(R.id.progress_faltas);

        txtNumFalta.setText(String.valueOf(totalFaltas));
        txtProgressoFalta.setText(totalFaltas + "/" + diasUteisNoMes);

        // Calcular porcentagem de faltas
        int progressFaltasPercent = diasUteisNoMes > 0 ? (int) ((totalFaltas / (float) diasUteisNoMes) * 100) : 0;
        progressFaltas.setProgress(progressFaltasPercent);

        Log.d("API", "Faltas: " + totalFaltas + "/" + diasUteisNoMes + " = " + progressFaltasPercent + "%");
    }

    private void atualizarUIPresencas(int totalPresencas, View view) {
        TextView txtNumPresenca = view.findViewById(R.id.txt_num_presenca);
        TextView txtProgressoPresenca = view.findViewById(R.id.txt_progresso_presenca);
        ProgressBar progressPresencas = view.findViewById(R.id.progress_presencas);

        txtNumPresenca.setText(String.valueOf(totalPresencas));
        txtProgressoPresenca.setText(totalPresencas + "/" + diasUteisNoMes);

        // Calcular porcentagem de presenças
        int progressPresencasPercent = diasUteisNoMes > 0 ? (int) ((totalPresencas / (float) diasUteisNoMes) * 100) : 0;
        progressPresencas.setProgress(progressPresencasPercent);

        Log.d("API", "Presenças: " + totalPresencas + "/" + diasUteisNoMes + " = " + progressPresencasPercent + "%");
    }

    private void verificaExisteNotificacao(Long id) {
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

        serviceAPI_NOSQL.contarNotificacao(id, "A").enqueue(new Callback<java.lang.Integer>() {
            @Override
            public void onResponse(Call<java.lang.Integer> call, Response<java.lang.Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body();
                    Log.d("API", "Quantidade de notificações: " + count);
                    if (count > 0) {
                        Inicio activity = (Inicio) getActivity();
                        if (activity != null && activity.getBinding() != null) {
                            activity.getBinding().aionNavBar.notificacao.setImageResource(R.drawable.ic_notificacao);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<java.lang.Integer> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}