package com.aula.aion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.databinding.ActivityPerfilBinding;
import com.aula.aion.model.Cargo;
import com.aula.aion.model.Funcionario;
import com.aula.aion.sinal.EnviaSinalMethod;
import com.aula.aion.ui.home.BottomSheetSairFragment;
import com.aula.aion.ui.home.DashboardsFragments;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

public class Perfil extends AppCompatActivity implements com.aula.aion.LogoutCallback {

    private Retrofit retrofit;
    private ActivityPerfilBinding binding;
    private Funcionario funcionarioAtual;
    private static final String FOTO_NOME = "foto_perfil.jpg";

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri photoURI;

    private static final int REQUEST_CAMERA_PERMISSION = 100;

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
        if (funcionarioAtual != null) {
            setarInformacoesFuncionario(funcionarioAtual);
        }

        binding.btnNotificacao.setOnClickListener(view -> {
            Intent intent = new Intent(Perfil.this, NotificacaoActivity.class);
            intent.putExtra("funcionario", funcionarioAtual);
            startActivity(intent);
        });

        binding.btnDesempenho.setOnClickListener(view -> {
            Intent intent = new Intent(Perfil.this, DashboardsFragments.class);
            intent.putExtra("funcionario", funcionarioAtual);
            startActivity(intent);
        });

        registrarCameraLauncher();
        registrarGalleryLauncher();
        carregarFotoSalva();

        binding.btnEditarPerfil.setOnClickListener(view -> {
            Intent intent = new Intent(Perfil.this, EditarPerfil.class);
            intent.putExtra("funcionario", funcionarioAtual);
            startActivity(intent);
        });

        binding.imgFotoPerfil.setOnClickListener(view -> mostrarDialogOpcoesFoto());
        setupBlurView();
        setupListeners();

        EnviaSinalMethod enviaSinalMethod = new EnviaSinalMethod();
        if (funcionarioAtual != null) {
            enviaSinalMethod.enviaSinal(funcionarioAtual.getCdMatricula());
        }
    }

    private void registrarCameraLauncher() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            if (photoURI != null) {
                                Bitmap bitmap = BitmapFactory.decodeStream(
                                        getContentResolver().openInputStream(photoURI)
                                );

                                // Corrigir orientação da imagem
                                bitmap = corrigirOrientacaoImagem(bitmap, photoURI);

                                // Aplicar borda arredondada e atualizar imagem
                                aplicarImagemPerfil(bitmap);
                                salvarFoto(bitmap);
                                Toast.makeText(this, "Foto capturada com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Erro ao capturar foto", Toast.LENGTH_SHORT).show();
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
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        this.getContentResolver(), imagemUri);

                                // Corrigir orientação da imagem
                                bitmap = corrigirOrientacaoImagem(bitmap, imagemUri);

                                // Aplicar borda arredondada e atualizar imagem
                                aplicarImagemPerfil(bitmap);
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

    private Bitmap corrigirOrientacaoImagem(Bitmap bitmap, Uri imageUri) {
        try {
            ExifInterface exif = new ExifInterface(getContentResolver().openInputStream(imageUri));
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return bitmap;
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private void aplicarImagemPerfil(Bitmap bitmap) {
        // Define a imagem no ImageView
        binding.imgFotoPerfil.setImageBitmap(bitmap);

        // Configura o ImageView para usar ScaleType que mantém proporção
        binding.imgFotoPerfil.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);

        // Aplica formato circular apenas quando há foto
        binding.imgFotoPerfil.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Cria um círculo perfeito
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        binding.imgFotoPerfil.setClipToOutline(true);
    }

    private void removerFormatoCircular() {
        // Remove o formato circular (volta ao padrão/ícone)
        binding.imgFotoPerfil.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.imgFotoPerfil.setClipToOutline(false);
        binding.imgFotoPerfil.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
    }

    private void mostrarDialogOpcoesFoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha uma opção");

        // Verifica se já existe foto salva para adicionar opção de remover
        File dir = getFilesDir();
        File arquivo = new File(dir, FOTO_NOME);

        String[] opcoes;
        if (arquivo.exists()) {
            opcoes = new String[]{"Câmera", "Galeria", "Remover Foto", "Cancelar"};
        } else {
            opcoes = new String[]{"Câmera", "Galeria", "Cancelar"};
        }

        builder.setItems(opcoes, (dialog, which) -> {
            if (which == 0) {
                abrirCamera();
            } else if (which == 1) {
                abrirGaleria();
            } else if (which == 2 && arquivo.exists()) {
                // Remover foto
                if (arquivo.delete()) {
                    removerFormatoCircular();
                    binding.imgFotoPerfil.setImageResource(R.drawable.ic_usericon); // Coloque aqui o ícone padrão
                    Toast.makeText(this, "Foto removida", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamera();
            } else {
                Toast.makeText(this, "Permissão da câmera negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Criar arquivo temporário para salvar a foto
                File photoFile = createImageFile();
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this,
                            getPackageName() + ".fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    cameraLauncher.launch(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao abrir câmera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "TEMP_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
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
                    aplicarImagemPerfil(bitmap);
                    Log.d("CarregarFoto", "Foto carregada com sucesso!");
                }
            } else {
                // Se não há foto salva, garante que está no formato padrão
                removerFormatoCircular();
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

        serviceAPI_SQL.selecionarCargoPorId(id).enqueue(new Callback<Cargo>() {
            @Override
            public void onResponse(Call<Cargo> call, Response<Cargo> response) {
                if (response.isSuccessful()) {
                    Cargo cargo = response.body();
                    if (cargo != null) {
                        binding.txtCargo.setText(cargo.getNome());
                    }
                }
            }

            @Override
            public void onFailure(Call<Cargo> call, Throwable t) {
                Log.e("API", "Erro: " + t.getMessage(), t);
            }
        });
    }

    private void setupBlurView() {
        float blurRadius = 11f;
        int overlayColor = Color.parseColor("#86F6F6F6");

        ViewGroup rootView = findViewById(android.R.id.content);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        BlurAlgorithm blurAlgorithm = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? new RenderEffectBlur()
                : new RenderScriptBlur(this);

        binding.perfilNavBar.blurView
                .setupWith(rootView, blurAlgorithm)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(blurRadius)
                .setOverlayColor(overlayColor)
                .setBlurAutoUpdate(true);
    }

    private void setupListeners() {
        binding.btnSair.setOnClickListener(view -> {
            BottomSheetSairFragment bottomSheetSairFragment = BottomSheetSairFragment.newInstance();
            bottomSheetSairFragment.setLogoutCallback(this);
            bottomSheetSairFragment.show(getSupportFragmentManager(), "BottomSheetSairFragment");
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.perfilNavBar.btnVoltar.setOnClickListener(view -> finish());
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