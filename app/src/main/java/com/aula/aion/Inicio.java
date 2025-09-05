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
import androidx.navigation.NavController; // Importação necessária
import androidx.navigation.fragment.NavHostFragment; // Importação necessária
import androidx.navigation.ui.AppBarConfiguration; // Importação necessária
import androidx.navigation.ui.NavigationUI; // Importação necessária

import com.aula.aion.databinding.ActivityInicioBinding;
import com.aula.aion.model.Funcionario;
import com.aula.aion.ui.home.BottomSheetBatidaFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView; // Importação necessária

public class Inicio extends AppCompatActivity {

    private Funcionario funcionario;

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    ActivityInicioBinding binding;

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

        BottomNavigationView navView = binding.navView; // Usa o ID 'nav_view' do XML corrigido

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = null;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        if (navController != null) {
            // IDs dos seus fragmentos de topo no mobile_navigation.xml
            // Adapte conforme os IDs reais do seu 'nav_menu.xml'
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home,
                    R.id.nav_justificativa,
                    R.id.nav_batida, //Batida de ponto
                    R.id.nav_reclamacao,
                    R.id.nav_dashboard)
                    .build();

            // Conecta a BottomNavigationView ao NavController
            NavigationUI.setupWithNavController(navView, navController);
        }

        binding.flbBatida.setOnClickListener(view -> {
            BottomSheetBatidaFragment bottomSheet = new BottomSheetBatidaFragment();
            Bundle args = new Bundle();
            args.putString("nome", funcionario.getNomeCompleto());
            bottomSheet.setArguments(args);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

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
    }
}