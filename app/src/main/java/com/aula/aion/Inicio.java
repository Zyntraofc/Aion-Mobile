package com.aula.aion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.aula.aion.databinding.ActivityInicioBinding;
import com.aula.aion.model.Funcionario;
import com.aula.aion.ui.home.BottomSheetBatidaFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Retrofit;

public class Inicio extends AppCompatActivity {

    private Funcionario funcionario;
    private ActivityInicioBinding binding;
    private static final String FOTO_NOME = "foto_perfil.jpg";
    private Retrofit retrofit;
    private boolean isFromCalendar = false;

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public ActivityInicioBinding getBinding() {
        return binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityInicioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        BottomNavigationView navView = binding.navView;

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = null;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        if (navController != null) {
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home,
                    R.id.nav_justificativa,
                    R.id.nav_batida,
                    R.id.nav_reclamacao,
                    R.id.nav_dashboard
            ).build();

            NavigationUI.setupWithNavController(navView, navController);
        }

        // Carregar foto do perfil
        carregarFotoSalva();

        binding.flbBatida.setOnClickListener(view -> {
            BottomSheetBatidaFragment bottomSheet = new BottomSheetBatidaFragment();
            Bundle args = new Bundle();
            args.putSerializable("funcionario", funcionario);
            bottomSheet.setArguments(args);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

        Date dataAtual = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM");
        String dataFormatada = formato.format(dataAtual);

        binding.aionNavBar.dataatual.setText("Hoje: " + dataFormatada);

        // Botão perfil
        binding.aionNavBar.profileButton.setOnClickListener(view -> {
            binding.aionNavBar.profileButton.animate()
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            Intent intent = new Intent(Inicio.this, Perfil.class);
                            intent.putExtra("funcionario", funcionario);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still);
                        }
                    })
                    .start();
        });

        // Botão notificações
        binding.aionNavBar.notificacao.setOnClickListener(view -> {
            binding.aionNavBar.notificacao.animate()
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            Intent intent = new Intent(Inicio.this, NotificacaoActivity.class);
                            Bundle args = new Bundle();
                            args.putSerializable("funcionario", funcionario);
                            intent.putExtras(args);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still);
                        }
                    })
                    .start();
        });

        NavController finalNavController = navController;
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_justificativa && arguments != null) {
                boolean fromCalendar = arguments.getBoolean("fromCalendar", false);

                if (fromCalendar) {
                    binding.navView.post(() -> {
                        binding.navView.setOnItemSelectedListener(item -> {
                            if (item.getItemId() == R.id.nav_home) {
                                controller.popBackStack(R.id.nav_home, false);
                                return true;
                            } else {
                                NavigationUI.onNavDestinationSelected(item, controller);
                                return true;
                            }
                        });
                    });
                } else {
                    binding.navView.post(() -> {
                        NavigationUI.setupWithNavController(binding.navView, controller);
                    });
                }
            } else {
                binding.navView.post(() -> {
                    NavigationUI.setupWithNavController(binding.navView, finalNavController);
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarregar a foto quando voltar para a activity
        carregarFotoSalva();
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
                    Log.d("CarregarFoto", "Foto carregada com sucesso na Activity Inicio!");
                }
            } else {
                // Se não há foto salva, garante que está no formato padrão
                removerFormatoCircular();
                Log.d("CarregarFoto", "Nenhuma foto encontrada, usando ícone padrão");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CarregarFoto", "Erro ao carregar foto: " + e.getMessage());
        }
    }

    private void aplicarImagemPerfil(Bitmap bitmap) {
        // Define a imagem no ImageView (profileButton)
        binding.aionNavBar.profileButton.setImageBitmap(bitmap);

        // Configura o ImageView para usar ScaleType que mantém proporção
        binding.aionNavBar.profileButton.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);

        // Aplica formato circular apenas quando há foto
        binding.aionNavBar.profileButton.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Cria um círculo perfeito
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        binding.aionNavBar.profileButton.setClipToOutline(true);
    }

    private void removerFormatoCircular() {
        // Remove o formato circular (volta ao padrão/ícone)
        binding.aionNavBar.profileButton.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.aionNavBar.profileButton.setClipToOutline(false);
        binding.aionNavBar.profileButton.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        binding.aionNavBar.profileButton.setImageResource(R.drawable.usericon);
    }
}