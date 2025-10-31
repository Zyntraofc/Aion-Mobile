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
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BottomSheetBatidaFragment extends BottomSheetDialogFragment {

    private BottomSheetBatidaBinding binding;
    private Retrofit retrofit;
    private Funcionario funcionario;

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

        // Recupera o objeto do bundle
        if (getArguments() != null) {
            funcionario = (Funcionario) getArguments().getSerializable("funcionario");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String dataAtual = new SimpleDateFormat("dd/MM").format(new Date());
        binding.txtHoraAtual.setText(mostrarHoraAtualFormatada());
        binding.textView6.setText(dataAtual);

        if (funcionario != null) {
            // usa o nome do funcionário
            String nomeFuncionario = funcionario.getNomeCompleto();
            if (nomeFuncionario != null) {
                binding.txtNome.setText("Olá, " + nomeFuncionario.split(" ")[0]);
            }
        }

        // Configurar o BottomSheetBehavior para expandir totalmente
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        }

        binding.btnBaterPonto.setOnClickListener(v -> {
            binding.btnBaterPonto.setEnabled(false);
            baterPonto();
        });

        SpannableString spannableString = new SpannableString("Não quer confirmar? Toque aqui");
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
        return agora.format(formatter);
    }

    private void baterPonto() {
        if (funcionario != null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        String credentials = Credentials.basic("colaborador", "colaboradorpass");
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", credentials)
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            String url = "https://ms-aion-jpa.onrender.com/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);

            Batida batida = new Batida(
                    LocalDateTime.now().toString(),
                    null,
                    funcionario.getCdMatricula(),
                    "1",
                    "0",
                    null
            );

            serviceAPI_SQL.inserirBatida(batida).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("API", "Chamada da API realizada");
                    Log.d("API", "Status code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        String batidaResponse = response.body();
                        Log.d("API", "Batida registrada: " + batidaResponse);

                        Toast.makeText(requireContext(), batidaResponse, Toast.LENGTH_SHORT).show();
                        binding.txtConfitmarBatida.setText(batidaResponse);
                        binding.btnBaterPonto.setEnabled(false);
                        binding.btnBaterPonto.postDelayed(() -> dismiss(), 2000);

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
                    binding.txtHoraAtual.setText("Ocorreu um erro!");
                }
            });
        }
    }

}
