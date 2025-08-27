package com.aula.aion;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aula.aion.databinding.ActivityPerfilBinding;
import com.aula.aion.ui.home.BottomSheetSairFragment;

// Imports para a lógica de Blur Híbrida
import eightbitlab.com.blurview.BlurAlgorithm;
import eightbitlab.com.blurview.RenderEffectBlur;
import eightbitlab.com.blurview.RenderScriptBlur;

public class Perfil extends AppCompatActivity implements com.aula.aion.LogoutCallback { // Implementa LogoutCallback

    private ActivityPerfilBinding binding;

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

        setupBlurView();
        setupListeners();
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
            BottomSheetSairFragment bottomSheet = BottomSheetSairFragment.newInstance(); // Usa o método factory
            bottomSheet.setLogoutCallback(this); // Passa o callback
            bottomSheet.show(getSupportFragmentManager(), "BottomSheetSair"); // Tag explícita
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
        // Caso logout seja false (cancelar), não faz nada
    }
}