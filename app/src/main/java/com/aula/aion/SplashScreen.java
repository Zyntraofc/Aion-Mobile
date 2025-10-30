package com.aula.aion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aula.aion.databinding.ActivitySplashScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    private ActivitySplashScreenBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        requestNotificationPermission();

        getFirebaseToken();

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
                    Intent homeIntent = new Intent(this, Inicio.class);
                    startActivity(homeIntent);
                } else {
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                }
                finish();
            }, 5000);

        }, 1000);
    }


    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissão de notificação concedida");
                getFirebaseToken();
            } else {
                Log.d(TAG, "Permissão de notificação negada");
            }
        }
    }

    private void getFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Erro ao obter token FCM", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d(TAG, "Token FCM: " + token);
                        sendTokenToServer(token);
                    }
                });
    }

    private void sendTokenToServer(String token) {
        Log.d(TAG, "Token pronto para ser enviado ao servidor: " + token);
    }
}