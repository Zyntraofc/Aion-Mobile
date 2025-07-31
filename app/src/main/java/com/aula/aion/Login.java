package com.aula.aion;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.aula.aion.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnEntrar.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString();
            String senha = binding.inputSenha.getText().toString();
            if ("".equals(email) || email.isEmpty() || "".equals(senha) || senha.isEmpty()) {
                binding.txtErroLogin.setText("Preencha todos os campos");
            }
            else {
                AutenticarUsuario(email, senha);
            }
        });

    }
    private void AutenticarUsuario(String email, String senha) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(this, Inicio.class);
                        startActivity(intent);
                        finish();
                    } else {
                        binding.txtErroLogin.setVisibility(binding.txtErroLogin.VISIBLE);
                        binding.txtErroLogin.setText("E-mail ou senha inválidos");
                    }
                });
    }
}