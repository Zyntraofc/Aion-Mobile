package com.aula.aion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aula.aion.databinding.ActivitySplashScreenBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.imgLogo.setAlpha(0f);
        binding.imgLogo.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.imgLogo.animate()
                    .alpha(1f)
                    .setDuration(3000)
                    .start();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {

                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {
                    // Email do usuário logado
                    String userEmail = currentUser.getEmail();

                    Intent homeIntent = new Intent(this, MainActivity.class);
                    startActivity(homeIntent);
                } else {
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                }
                finish();
            }, 5000);

        }, 1000);
    }


}