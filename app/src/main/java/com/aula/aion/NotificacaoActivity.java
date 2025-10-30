package com.aula.aion;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.adapter.NotificacaoAdapter;
import com.aula.aion.api.ServiceAPI_NOSQL;
import com.aula.aion.databinding.ActivityNotificacaoBinding;
import com.aula.aion.model.Funcionario;
import com.aula.aion.model.Notificacao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurAlgorithm;
import eightbitlab.com.blurview.RenderEffectBlur;
import eightbitlab.com.blurview.RenderScriptBlur;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificacaoActivity extends AppCompatActivity {

    private ActivityNotificacaoBinding binding;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNotificacaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupBlurView();
        setupListeners();
        Bundle bundle = getIntent().getExtras();
        Funcionario funcionario = (Funcionario) bundle.getSerializable("funcionario");
        if (funcionario != null) {
            getNotificacaoByUser(funcionario.getCdMatricula());
        }
    }

    private void setupBlurView() {
        float blurRadius = 11f;
        int overlayColor = Color.parseColor("#86F6F6F6");

        ViewGroup rootView = findViewById(android.R.id.content);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        BlurAlgorithm blurAlgorithm;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurAlgorithm = new RenderEffectBlur();
        } else {
            blurAlgorithm = new RenderScriptBlur(this);
        }

        binding.notificacaoNavBar.blurView
                .setupWith(rootView, blurAlgorithm)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(blurRadius)
                .setOverlayColor(overlayColor)
                .setBlurAutoUpdate(true);
    }
    private void setupListeners() {
        binding.notificacaoNavBar.btnVoltar.setOnClickListener(view -> {
            finish();
        });

    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right);
    }
    private void getNotificacaoByUser(Long id) {
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
                .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                            throws JsonParseException {
                        return LocalDateTime.parse(json.getAsString());
                    }
                })
                .create();

        String url = "https://ms-aion-mongodb.onrender.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServiceAPI_NOSQL serviceAPI_NOSQL = retrofit.create(ServiceAPI_NOSQL.class);

        Call<List<Notificacao>> call = serviceAPI_NOSQL.selecionarNotificacaoPorId(id);

        call.enqueue(new Callback<List<Notificacao>>() {
            @Override
            public void onResponse(Call<List<Notificacao>> call, Response<List<Notificacao>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Notificacao> notificacoes = response.body();
                    Log.d("API", "Notificações recebidas: " + notificacoes.size());

                    RecyclerView recyclerView = binding.recyclerNotificacao;
                    recyclerView.setLayoutManager(new LinearLayoutManager(NotificacaoActivity.this));
                    NotificacaoAdapter adapter = new NotificacaoAdapter(notificacoes, NotificacaoActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("API", "Erro na resposta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Notificacao>> call, Throwable t) {
                Log.e("API", "Erro na chamada: " + t.getMessage(), t);
            }
        });
    }
}