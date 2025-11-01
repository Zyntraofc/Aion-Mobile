package com.aula.aion.ui.home;

import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aula.aion.Inicio;
import com.aula.aion.R;
import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.model.Batida;
import com.aula.aion.model.MotivoFalta;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.aula.aion.databinding.BottomSheetJustificativaBinding;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BottomSheetJustificativaFragment extends BottomSheetDialogFragment {

    private List<MotivoFalta> listaMotivoFalta;
    private Retrofit retrofit;
    private Long cdMatricula;
    private String data; // formato: dd/MM ou dd/MM/yyyy
    private int numInclusao;

    private BottomSheetJustificativaBinding binding;

    public static BottomSheetJustificativaFragment newInstance() {
        BottomSheetJustificativaFragment fragment = new BottomSheetJustificativaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetJustificativaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenBottomSheetDialog);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            data = args.getString("data");
            numInclusao = args.getInt("numInclusao");

            if (data != null) {
                binding.txtData.setText("Dia " + data);
            } else {
                String dataAtual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                binding.txtData.setText("Dia " + dataAtual);
                data = dataAtual;
            }

            Inicio activity = (Inicio) getActivity();
            if (activity != null) {
                cdMatricula = activity.getFuncionario().getCdMatricula();
            }

            if (numInclusao == 1){
                binding.cardSaida.setVisibility(View.INVISIBLE);
            }
        }

        // Configurar o BottomSheetBehavior para expandir totalmente
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        }

        listarMotivoFalta(binding.spinnerMotivo);

        // Configurar o botão de justificativa
        binding.btnJustificar.setOnClickListener(v -> {
            incluirJustificativa();
        });

        formataHora(binding.edtHoraEntrada);
        formataHora(binding.edtHoraSaida);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void listarMotivoFalta(Spinner spinner) {
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

        serviceAPI_SQL.listarMotivoFalta().enqueue(new Callback<List<MotivoFalta>>() {
            @Override
            public void onResponse(Call<List<MotivoFalta>> call, Response<List<MotivoFalta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaMotivoFalta = response.body();

                    ArrayAdapter<MotivoFalta> adapter = new ArrayAdapter<>(
                            spinner.getContext(),
                            android.R.layout.simple_spinner_item,
                            listaMotivoFalta
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Log.d("API", "Resposta não foi sucesso: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MotivoFalta>> call, Throwable t) {
                Log.e("API", "Erro na chamada: " + t.getMessage(), t);
            }
        });
    }

    private void incluirJustificativa() {
        String horaEntrada = binding.edtHoraEntrada.getText().toString().trim();
        String horaSaida = binding.edtHoraSaida.getText().toString().trim();

        // Validação de preenchimento
        if (horaEntrada.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha o horário de entrada", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isHoraValida(horaEntrada)) {
            Toast.makeText(requireContext(), "Horário de entrada inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (numInclusao == 0) {
            if (horaSaida.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha o horário de saída", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isHoraValida(horaSaida)) {
                Toast.makeText(requireContext(), "Horário de saída inválido", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Só chega aqui se tudo estiver válido
        inserirBatidaComHorario(horaEntrada);

        if (numInclusao == 0 && !horaSaida.isEmpty()) {
            inserirBatidaComHorario(horaSaida);
        }
    }


    private void inserirBatidaComHorario(String horario) {
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);

        // Converter data e horário para LocalDateTime
        LocalDateTime dataHoraBatida = converterParaLocalDateTime(data, horario);

        // Formatar para string no formato ISO (ou o formato que sua API espera)
        String dataHoraFormatada = dataHoraBatida.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Obter o motivo falta selecionado no spinner
        MotivoFalta motivoSelecionado = (MotivoFalta) binding.spinnerMotivo.getSelectedItem();
        Long cdMotivo = motivoSelecionado != null ? motivoSelecionado.getCdMotivoFalta() : null;

        Batida batida = new Batida(
                dataHoraFormatada,
                binding.txtJustificativa.getText().toString(),
                cdMatricula,
                "1",
                "0",
                cdMotivo
        );

        serviceAPI_SQL.inserirBatida(batida).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("API", "Chamada da API realizada");
                Log.d("API", "Status code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String batidaResponse = response.body();
                    Log.d("API", "justificativa registrada: " + batidaResponse);
                    Toast.makeText(requireContext(), batidaResponse, Toast.LENGTH_SHORT).show();
                    new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                        dismiss();
                    }, 3000);
                } else {
                    try {
                        Log.e("API", "Erro body: " +
                                (response.errorBody() != null ? response.errorBody().string() : "null"));
                    } catch (IOException e) {
                        Log.e("API", "Erro ao ler o erro body", e);
                    }

                    Toast.makeText(requireContext(),
                            "Erro ao registrar batida: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API", "Erro na chamada da API: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Falha na comunicação com o servidor!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Converte a data (dd/MM ou dd/MM/yyyy) e horário (HH:mm) para LocalDateTime
     */
    private LocalDateTime converterParaLocalDateTime(String dataStr, String horarioStr) {
        try {
            // Parse do horário
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime hora = LocalTime.parse(horarioStr, timeFormatter);

            // Parse da data
            LocalDate dataLocal;
            if (dataStr.contains("/")) {
                String[] partes = dataStr.split("/");
                int dia = Integer.parseInt(partes[0]);
                int mes = Integer.parseInt(partes[1]);
                int ano;

                if (partes.length == 2) {
                    ano = LocalDate.now().getYear();
                } else {
                    ano = Integer.parseInt(partes[2]);
                    // Se o ano tem 2 dígitos, converte para 4
                    if (ano < 100) {
                        ano += 2000;
                    }
                }

                dataLocal = LocalDate.of(ano, mes, dia);
            } else {
                dataLocal = LocalDate.now();
            }

            return LocalDateTime.of(dataLocal, hora);

        } catch (Exception e) {
            Log.e("API", "Erro ao converter data/hora: " + e.getMessage(), e);
            // Em caso de erro, retorna data/hora atual
            return LocalDateTime.now();
        }
    }

    private void formataHora(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;

                String str = s.toString().replace(":", "");
                if (str.length() > 4)
                    str = str.substring(0, 4);

                StringBuilder formatted = new StringBuilder();
                if (str.length() >= 3) {
                    formatted.append(str.substring(0, 2))
                            .append(":")
                            .append(str.substring(2));
                } else if (str.length() >= 1) {
                    formatted.append(str);
                }

                isUpdating = true;
                editText.setText(formatted.toString());
                editText.setSelection(formatted.length());
                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    /**
     * Valida se o horário está no formato HH:mm e é um horário possível
     */
    private boolean isHoraValida(String hora) {
        if (hora == null || hora.isEmpty()) return false;

        try {
            String[] partes = hora.split(":");
            if (partes.length != 2) return false;

            int h = Integer.parseInt(partes[0]);
            int m = Integer.parseInt(partes[1]);

            return (h >= 0 && h < 24) && (m >= 0 && m < 60);
        } catch (NumberFormatException e) {
            return false;
        }
    }

}