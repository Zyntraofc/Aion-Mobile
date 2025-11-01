package com.aula.aion.ui.home;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.aula.aion.R;
import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.databinding.BottomSheetReclamacaoBinding;
import com.aula.aion.model.Reclamacao;
import com.aula.aion.model.TpReclamacao;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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

public class BottomSheetReclamacaoFragment extends BottomSheetDialogFragment  {

    BottomSheetReclamacaoBinding binding;
    private Retrofit retrofit;
    private List<TpReclamacao> listaTpReclamacao;

    public BottomSheetReclamacaoFragment() {}

    public interface OnDimissListener {
        void onBottomSheetDismissed(Reclamacao reclamacao);
    }
    private DialogInterface.OnDismissListener dismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.dismissListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDismiss(dialog);
        }
    }
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenBottomSheetDialog);
        binding = BottomSheetReclamacaoBinding.inflate(getLayoutInflater());
        binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
            behavior.setDraggable(true); // opcional
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetReclamacaoBinding.inflate(inflater, container, false);

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

        binding.txtLink2.setText(spannableString);
        binding.txtLink2.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        binding.txtLink2.setHighlightColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));

        binding.txtData.setText(String.format("%s", new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date())));

        listarTpReclamacao(binding.spinner);

        binding.btnRegistrar.setOnClickListener(v -> {
            if (getArguments() != null) {
                Long lCdFuncionario = (Long) getArguments().getLong("lCdFuncionario") ;
                Long lCdTpReclamacao = 0L;
                int posicaoSelecionada = binding.spinner.getSelectedItemPosition();

                if (posicaoSelecionada >= 0 && listaTpReclamacao != null) {
                    TpReclamacao selecionado = listaTpReclamacao.get(posicaoSelecionada);
                    lCdTpReclamacao = selecionado.getCdTpReclamacao();
                }

                Reclamacao reclamacao = new Reclamacao(null, LocalDate.now(), binding.editDescricao.getText().toString(), lCdFuncionario, lCdTpReclamacao, "A", null);
                Log.d("API", "Vai chamar o metodo");
                inserirReclamacao(reclamacao);
            }
        });
        return binding.getRoot();
    }

    private void inserirReclamacao(Reclamacao reclamacao) {
        Log.d("API", "Entrou no metodo");

        // Configura o cliente com autenticação básica
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("colaborador", "colaboradorpass");
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", credentials)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        // Configura o Gson com adaptador para LocalDate
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                        new com.google.gson.JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                        LocalDate.parse(json.getAsString()))
                .create();

        String url = "https://ms-aion-jpa.onrender.com";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))  // Usa o Gson customizado
                .build();

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);
        Log.d("API-JSON", "Response JSON: " + gson.toJson(reclamacao));

        serviceAPI_SQL.inserirReclamacao(reclamacao).enqueue(new Callback<Reclamacao>() {
            @Override
            public void onResponse(Call<Reclamacao> call, Response<Reclamacao> response) {
                Log.d("API", "Chamada da API realizada");
                Log.d("API", "Status code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Reclamacao reclamacaoResponse = response.body();
                    Log.d("API", "Reclamacao registrada: " + reclamacaoResponse.getDescricao());

                    Toast.makeText(requireContext(), "Reclamacao registrada com sucesso!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    try {
                        Log.e("API", "Erro body: " +
                                (response.errorBody() != null ? response.errorBody().string() : "null"));
                    } catch (IOException e) {
                        Log.e("API", "Erro ao ler o erro body", e);
                    }
                    Toast.makeText(requireContext(),
                            "Erro ao registrar reclamação: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Reclamacao> call, Throwable t) {
                Log.e("API", "Erro na chamada da API: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Falha na comunicação com o servidor!", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("API", "Saiu do metodo");
    }

    private void listarTpReclamacao(Spinner spinner) {
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
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);

        serviceAPI_SQL.listarTpReclamacao().enqueue(new Callback<List<TpReclamacao>>() {
            @Override
            public void onResponse(Call<List<TpReclamacao>> call, Response<List<TpReclamacao>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaTpReclamacao = response.body();

                    // Mostrar só os nomes no Spinner
                    List<String> nomes = new ArrayList<>();
                    for (TpReclamacao tp : listaTpReclamacao) {
                        nomes.add(tp.getNome());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            spinner.getContext(),
                            android.R.layout.simple_spinner_item,
                            nomes
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Log.d("API", "Resposta não foi sucesso: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<TpReclamacao>> call, Throwable t) {
                Log.e("API", "Erro na chamada: " + t.getMessage(), t);
            }
        });
    }
}
