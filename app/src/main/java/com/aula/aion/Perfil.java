package com.aula.aion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.databinding.ActivityPerfilBinding;
import com.aula.aion.model.Cargo;
import com.aula.aion.model.Funcionario;
import com.google.firebase.auth.FirebaseAuth;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Perfil extends AppCompatActivity implements com.aula.aion.LogoutCallback {

    private Retrofit retrofit;
    private ActivityPerfilBinding binding;
    private Funcionario funcionarioAtual;
    private static final String FOTO_NOME = "foto_perfil.jpg";

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        funcionarioAtual = (Funcionario) getIntent().getSerializableExtra("funcionario");
        Log.d("Ver data em perfil", funcionarioAtual.getNascimento());
        if (funcionarioAtual != null) {
            setarInformacoesFuncionario(funcionarioAtual);
        }

        registrarCameraLauncher();
        registrarGalleryLauncher();

        carregarFotoSalva();

        binding.btnEditarPerfil.setOnClickListener(view -> {
            Intent intent = new Intent(Perfil.this, EditarPerfil.class);
            intent.putExtra("funcionario", funcionarioAtual);
            startActivity(intent);
        });

        binding.imgFotoPerfil.setOnClickListener(view -> {
            mostrarDialogOpcoesFoto();
        });

        setupBlurView();
        setupListeners();
    }

    private void registrarCameraLauncher() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imagemCapturada = (Bitmap) extras.get("data");
                            if (imagemCapturada != null) {
                                binding.imgFotoPerfil.setImageBitmap(imagemCapturada);
                                salvarFoto(imagemCapturada);
                                Toast.makeText(this, "Foto capturada com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    private void registrarGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imagemUri = result.getData().getData();
                        if (imagemUri != null) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagemUri);
                                binding.imgFotoPerfil.setImageBitmap(bitmap);
                                salvarFoto(bitmap);
                                Toast.makeText(this, "Foto carregada com sucesso!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Erro ao carregar foto", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    private void mostrarDialogOpcoesFoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha uma opção");
        builder.setItems(new String[]{"Câmera", "Galeria", "Cancelar"}, (dialog, which) -> {
            if (which == 0) {
                abrirCamera();
            } else if (which == 1) {
                abrirGaleria();
            }
        });
        builder.show();
    }

    private void abrirCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void salvarFoto(Bitmap bitmap) {
        try {
            File dir = getFilesDir();
            File arquivo = new File(dir, FOTO_NOME);

            FileOutputStream fos = new FileOutputStream(arquivo);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            Log.d("SalvarFoto", "Foto salva em: " + arquivo.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SalvarFoto", "Erro ao salvar foto: " + e.getMessage());
        }
    }

    private void carregarFotoSalva() {
        try {
            File dir = getFilesDir();
            File arquivo = new File(dir, FOTO_NOME);

            if (arquivo.exists()) {
                FileInputStream fis = new FileInputStream(arquivo);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                fis.close();

                if (bitmap != null) {
                    binding.imgFotoPerfil.setImageBitmap(bitmap);
                    Log.d("CarregarFoto", "Foto carregada com sucesso!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CarregarFoto", "Erro ao carregar foto: " + e.getMessage());
        }
    }

    private void setarInformacoesFuncionario(Funcionario funcionario) {
        binding.txtNome.setText(funcionario.getNomeCompleto());
        binding.txtEmail.setText(funcionario.getEmail());
        chamaAPI_GetCargoById(funcionario.getCdCargo());
    }

    private void chamaAPI_GetCargoById(Long id) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("admin", "123456");
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

        serviceAPI_SQL.selecionarCargoPorId(id).enqueue(new Callback<Cargo>() {
            @Override
            public void onResponse(Call<Cargo> call, Response<Cargo> response) {
                if (response.isSuccessful()) {
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

        binding.perfilNavBar.blurView
                .setupWith(rootView, blurAlgorithm)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(blurRadius)
                .setOverlayColor(overlayColor)
                .setBlurAutoUpdate(true);
    }

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
                if (selectedItem.equals("Ingles (United States)")) {
                    Toast.makeText(Perfil.this, "Você selecionou: " + selectedItem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

    @Override
    public void onLogoutDecision(boolean logout) {
        if (logout) {
            finish();
            Intent intent = new Intent(Perfil.this, Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still);
        }
    }
}