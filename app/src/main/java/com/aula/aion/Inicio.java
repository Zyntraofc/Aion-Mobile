package com.aula.aion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class Inicio extends AppCompatActivity {

    private Funcionario funcionario;
    ActivityInicioBinding binding;

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Funcionario getFuncionario() {
        return funcionario;
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

        binding.aionNavBar.dataatual.setText("Hoje: " + dataFormatada);        // Botão perfil
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
    }
}
