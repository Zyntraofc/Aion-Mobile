package com.aula.aion.ui.home;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aula.aion.R;
import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.databinding.BottomSheetBatidaBinding;
import com.aula.aion.model.Batida;
import com.aula.aion.model.Funcionario;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.core.content.ContextCompat;
import android.text.style.ForegroundColorSpan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BottomSheetBatidaFragment extends BottomSheetDialogFragment {

    private BottomSheetBatidaBinding binding;
    private String nomeFuncionario;
    private Retrofit retrofit;

    public static BottomSheetBatidaFragment newInstance() {
        BottomSheetBatidaFragment fragment = new BottomSheetBatidaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetBatidaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenBottomSheetDialog);
        if (getArguments() != null) {
            nomeFuncionario = getArguments().getString("nome");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.txtHoraAtual.setText(mostrarHoraAtualFormatada());
        // Recuperar os argumentos passados
        Bundle args = getArguments();
        if (args != null) {
            String data = args.getString("data", "Dia 06/06");
            binding.txtData.setText(data);
        }
        if (nomeFuncionario != null) {
            binding.txtNome.setText("Olá, " + nomeFuncionario.split(" ")[0]);
        }

        // Configurar o BottomSheetBehavior para expandir totalmente
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true); // Evita colapso parcial
        }

        binding.btnBaterPonto.setOnClickListener(v -> {
            baterPonto();
        });

        // Configurar o SpannableString para o texto clicável
        SpannableString spannableString = new SpannableString("Não quer confirmar? Toque aqui");

        // Define a parte "Toque aqui" como clicável e azul
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                dismiss();
            }
        };

        int startIndex = spannableString.toString().indexOf("Toque aqui");
        int endIndex = startIndex + "Toque aqui".length();
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.txtLink.setText(spannableString);
        binding.txtLink.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        binding.txtLink.setHighlightColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private String mostrarHoraAtualFormatada(){
        LocalTime agora = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H'h'mm");
        String horaFormatada = agora.format(formatter);
        return horaFormatada;
    }

    private void baterPonto() {
        if (getArguments() != null) {
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
            Funcionario funcionario = (Funcionario) getArguments().getSerializable("funcionario");
            serviceAPI_SQL.inserirBatida(new Batida(null, LocalDateTime.now(), null, funcionario.getCdMatricula())).enqueue(new Callback<Batida>() {
                @Override
                public void onResponse(Call<Batida> call, Response<Batida> response) {
                    if (response.isSuccessful()) {
                        Batida batida = response.body();
                        if (batida != null) {
                            binding.txtHoraAtual.setText("Batida Registrada!");
                            try {
                                Thread.sleep(3000); // pausa de 3 segundos
                                dismiss();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<Batida> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("chamaAPI_GetByEmail", "Erro na chamada da API: " + t.getMessage());
                    binding.txtHoraAtual.setText("Ocorreu um erro!");
                }
            });
        }
    }
}