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
import com.google.android.material.bottomnavigation.BottomNavigationView; // Importação necessária

public class Inicio extends AppCompatActivity {

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

//            navView.setOnItemSelectedListener(item -> {
//                if (item.getItemId() == R.id.navigation_add_item) { // ID do seu item "mais" no nav_menu
//                    // Ação personalizada para o botão '+'
//                    // Por exemplo: Toast.makeText(this, "Botão '+' clicado!", Toast.LENGTH_SHORT).show();
//                    // Ou iniciar uma nova Activity para adicionar algo
//                    // Intent intent = new Intent(this, AddItemActivity.class);
//                    // startActivity(intent);
//                    return true; // Indica que o evento foi consumido e não deve navegar para um fragmento
//                } else {
//                    // Para os outros itens, NavigationUI gerencia a navegação para os fragmentos
//                    return NavigationUI.onNavDestinationSelected(item, navController);
//                }
//            });
        }

        binding.aionNavBar.profileButton.setOnClickListener(view -> {
            binding.aionNavBar.profileButton.animate()
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            Intent intent = new Intent(Inicio.this, Perfil.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still);
                        }
                    })
                    .start();
        });
    }
}