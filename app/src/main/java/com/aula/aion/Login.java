package com.aula.aion;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aula.aion.databinding.ActivityLoginBinding;
import com.aula.aion.ui.home.BottomSheetRedefinirSenhaFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnEntrar.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString();
            String senha = binding.inputSenha.getText().toString();

            if (email.isEmpty() || senha.isEmpty()) {
                exibirErro();
                binding.txtErroLogin.setText("Preencha todos os campos");
            } else {
                // Mostrar a ProgressBar e desabilitar o botão
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnEntrar.setEnabled(false);

                AutenticarUsuario(email, senha);
            }
        });

        // Texto "Esqueceu a senha"
        binding.txtEsqueceuSenha.setOnClickListener(v -> {
            BottomSheetRedefinirSenhaFragment bottomSheet = new BottomSheetRedefinirSenhaFragment();
            bottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        });
    }

    private void AutenticarUsuario(String email, String senha) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    // Sempre esconder a ProgressBar e reabilitar o botão
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnEntrar.setEnabled(true);

                    if (task.isSuccessful()) {
                        Intent intent = new Intent(this, Inicio.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("LoginActivity", "Autenticação falhou", task.getException());
                        exibirErro();
                        String erro = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            erro = "E-mail não está cadastrado";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            erro = "E-mail ou senha inválidos";
                        } catch (Exception e) {
                            erro = "Erro ao autenticar";
                            Log.e("LoginActivity", "EXCEPTION", e);
                        }
                        binding.txtErroLogin.setText(erro);
                        binding.txtErroLogin.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void exibirErro(){
        int color = ContextCompat.getColor(this, R.color.red);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{}
                },
                new int[]{color, color, color, color}
        );

        binding.textInputLayout2.setBoxStrokeColorStateList(colorStateList);
        binding.textInputLayout.setBoxStrokeColorStateList(colorStateList);
    }
}