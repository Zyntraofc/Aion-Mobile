package com.aula.aion;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build; // Import necessário
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup; // Import necessário
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.databinding.ActivityPerfilBinding;
import com.aula.aion.model.Cargo;
import com.aula.aion.model.Funcionario;
import com.aula.aion.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;

// Imports para a lógica de Blur Híbrida
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

public class Perfil extends AppCompatActivity {

    private Retrofit retrofit;

    private ActivityPerfilBinding binding; // Boa prática: tornar o binding privado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // A chamada EdgeToEdge.enable(this) deve vir antes de setContentView
        EdgeToEdge.enable(this);

        // Inflar o binding e definir o content view
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar os insets da janela (para barras de sistema)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Funcionario funcionario = (Funcionario) getIntent().getSerializableExtra("funcionario");
        Log.d("Perfil", "Funcionario recebido: " + funcionario.getNomeCompleto());
        if (funcionario != null) {
            setarInformacoesFuncionario(funcionario);
        }


        // Configurar o efeito de desfoque
        setupBlurView();

        // Configurar listeners de clique e outros componentes
        setupListeners();
    }

    private void setarInformacoesFuncionario(Funcionario funcionario) {
        binding.txtNome.setText(funcionario.getNomeCompleto());
        binding.txtEmail.setText(funcionario.getEmail());
        chamaAPI_GetCargoById(funcionario.getCdCargo());
    }
    private void chamaAPI_GetCargoById(Long id) {
        Log.d("chamaAPI_GetByEmail", "Chamando API com id: " + id);
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

        serviceAPI_SQL.selecionarCargoPorId(id).enqueue(new Callback<Cargo>() {
            @Override
            public void onResponse(Call<Cargo> call, Response<Cargo> response) {
                if (response.isSuccessful()) {
                    Log.d("chamaAPI_GetByEmail", "Resposta da API: " + response);
                    Cargo cargo = response.body();
                    if (cargo != null) {
                        Log.d("chamaAPI_GetByEmail", "Cargo: " + cargo.getNome());
                        binding.txtCargo.setText(cargo.getNome());
                    }
                }
            }

            @Override
            public void onFailure(Call<Cargo> call, Throwable t) {
                t.printStackTrace();
                Log.d("chamaAPI_GetByEmail", "Erro na chamada da API: " + t.getMessage());
            }
        });
    }

    /**
     * Configura a BlurView com uma lógica híbrida para obter a melhor qualidade visual
     * possível de acordo com a versão do Android.
     */
    private void setupBlurView() {
        float blurRadius = 11f; // Raio do desfoque. Ajuste entre 16f e 25f para o efeito desejado.
        int overlayColor = Color.parseColor("#86F6F6F6"); // Cor de sobreposição (branco com 25% de opacidade).

        // O rootView é o container que a BlurView irá "observar" para criar o desfoque.
        // Usar o 'android.R.id.content' garante que ele capture tudo na tela.
        ViewGroup rootView = findViewById(android.R.id.content);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        // Lógica para escolher o melhor algoritmo de desfoque
        BlurAlgorithm blurAlgorithm;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Usa RenderEffectBlur para Android 12+ (API 31+), que tem qualidade superior.
            blurAlgorithm = new RenderEffectBlur();
        } else {
            // Usa RenderScriptBlur como fallback para versões mais antigas.
            blurAlgorithm = new RenderScriptBlur(this);
        }

        binding.perfilNavBar.blurView
                .setupWith(rootView, blurAlgorithm)
                .setFrameClearDrawable(windowBackground) // Evita artefatos visuais.
                .setBlurRadius(blurRadius)
                .setOverlayColor(overlayColor) // Aplica a cor de "vidro fosco".
                .setBlurAutoUpdate(true);// Atualiza o desfoque automaticamente.
    }

    /**
     * Centraliza a configuração de todos os listeners da UI.
     */
    private void setupListeners() {
        binding.btnSair.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Perfil.this, Login.class);
            startActivity(intent);
            finish();
        });

        binding.sprIdioma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                // A linha abaixo não é necessária, o Spinner já gerencia a seleção.
                // binding.sprIdioma.setSelection(position);
                if (selectedItem.equals("Ingles (United States)")) {
                    Toast.makeText(Perfil.this, "Você selecionou: " + selectedItem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Geralmente não é necessário fazer nada aqui, mas pode-se definir um padrão se quiser.
            }
        });

        binding.perfilNavBar.btnVoltar.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right);
    }
}
