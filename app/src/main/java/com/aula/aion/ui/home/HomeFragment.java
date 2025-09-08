package com.aula.aion.ui.home;// HomeFragment.java (Exemplo do seu fragmento inicial com a lógica do calendário)
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.Inicio;
import com.aula.aion.R;
import com.aula.aion.adapter.CalendarAdapter;
import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.model.CalendarDay;
import com.aula.aion.model.Funcionario;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
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

    // Construtor público vazio necessário
    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // --- Inicialização de outras coisas no HomeFragment ---
        // Ex: TextView tvWelcome = view.findViewById(R.id.tvWelcome);
        // tvWelcome.setText("Olá!");
        // Ex: Button btnAction = view.findViewById(R.id.btnAction);
        // btnAction.setOnClickListener(...)
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        chamaAPI_GetByEmail(email, view);



        monthYearTextView = view.findViewById(R.id.monthYearTextView);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        ImageButton previousMonthButton = view.findViewById(R.id.previousMonthButton);
        ImageButton nextMonthButton = view.findViewById(R.id.nextMonthButton);

        currentCalendar = Calendar.getInstance();

        setupCalendar(); // Chama o método de configuração do calendário

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

    // --- Método setupCalendar() movido para HomeFragment ---
    private void setupCalendar() {
        if (getContext() == null) return;

        monthYearTextView.setText(new SimpleDateFormat("MMMM 'de' yyyy", new Locale("pt", "BR")).format(currentCalendar.getTime()));

        List<CalendarDay> days = new ArrayList<>();
        Calendar monthCalendar = (Calendar) currentCalendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (firstDayOfMonth == -1) firstDayOfMonth = 6;
        if (firstDayOfMonth == 0) firstDayOfMonth = 7;

        Calendar prevMonthCalendar = (Calendar) monthCalendar.clone();
        prevMonthCalendar.add(Calendar.MONTH, -1);
        int daysInPrevMonth = prevMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = firstDayOfMonth - 1; i >= 0; i--) {
            CalendarDay day = new CalendarDay(String.valueOf(daysInPrevMonth - i), false, daysInPrevMonth - i);
            if (currentCalendar.get(Calendar.YEAR) == 2025 && currentCalendar.get(Calendar.MONTH) == Calendar.JUNE) {
                if (prevMonthCalendar.get(Calendar.YEAR) == 2025 && prevMonthCalendar.get(Calendar.MONTH) == Calendar.MAY) {
                    if (day.getDayOfMonth() == 30 || day.getDayOfMonth() == 31) {
                        day.setGreenOutline(true);
                    }
                }
            }
            days.add(day);
        }

        int daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= daysInMonth; i++) {
            CalendarDay day = new CalendarDay(String.valueOf(i), true, i);

            if (currentCalendar.get(Calendar.YEAR) == 2025 && currentCalendar.get(Calendar.MONTH) == Calendar.JUNE) {
                switch (i) {
                    case 1:
                    case 2:
                    case 3:
                    case 8:
                    case 9:
                        day.setGreenOutline(true);
                        break;
                    case 6:
                    case 7:
                        day.setRedOutline(true);
                        break;
                    case 10:
                        day.setPurpleFill(true);
                        break;
                }
            }
            days.add(day);
        }

        int totalDays = days.size();
        int daysToAddNextMonth = 0;
        if (totalDays < 42) {
            daysToAddNextMonth = 42 - totalDays;
        }

        for (int i = 1; i <= daysToAddNextMonth; i++) {
            days.add(new CalendarDay(String.valueOf(i), false, i));
        }

        calendarAdapter = new CalendarAdapter(getContext(), days);
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarRecyclerView.setAdapter(calendarAdapter);
    }
    private void chamaAPI_GetByEmail(String email, View view) {
        Log.d("chamaAPI_GetByEmail", "Chamando API com email: " + email);
        // Credenciais da API
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("admin", "123456");
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", credentials)
                            .build();
                    return chain.proceed(request);
                })
                .build();
        //Definir a URL da API
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
                        TextView txtBemVindo = view.findViewById(R.id.txtBemVindo);
                        txtBemVindo.setText("Olá, " + funcionarioRetorno.getNomeCompleto());
                        Log.d("VER DATA", funcionarioRetorno.getNascimento());
                        Inicio activity = (Inicio) getActivity();
                        if (activity != null) {
                            activity.setFuncionario(funcionarioRetorno);
                        }
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
}