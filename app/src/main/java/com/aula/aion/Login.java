package com.aula.aion;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
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
    private boolean isPasswordVisible = false;    +
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

        // Texto "Esqueceu a senha"
        binding.txtEsqueceuSenha.setOnClickListener(v -> {
            BottomSheetRedefinirSenhaFragment bottomSheet = new BottomSheetRedefinirSenhaFragment();
            bottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        });

        // Lógica do "olhinho" para mostrar/esconder senha
        binding.inputSenha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (binding.inputSenha.getCompoundDrawables()[2] != null && event.getRawX() >= (binding.inputSenha.getRight() - binding.inputSenha.getCompoundDrawables()[2].getBounds().width()))
                    {
                        isPasswordVisible = !isPasswordVisible;

                        if (isPasswordVisible) {
                            // Mostrar senha
                            binding.inputSenha.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            Drawable eyeOffDrawable = ContextCompat.getDrawable(Login.this, R.drawable.ic_eye_off);
                            if (eyeOffDrawable != null) {
                                binding.inputSenha.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeOffDrawable, null);
                            }
                        } else {
                            // Esconder senha
                            binding.inputSenha.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            Drawable eyeDrawable = ContextCompat.getDrawable(Login.this, R.drawable.ic_eye);
                            if (eyeDrawable != null) {
                                binding.inputSenha.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeDrawable, null);
                            }
                        }
                        binding.inputSenha.setSelection(binding.inputSenha.getText().length());
                        v.performClick();
                        return true;
                    }
                }
                return false;

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
                        String erro = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e)
                        {
                            erro = "E-mail não está cadastrado";
                            Log.e("LoginActivity", erro, e);
                        }
                        catch (FirebaseAuthInvalidCredentialsException e)
                        {
                            erro = "E-mail ou senha inválidos";
                            Log.e("LoginActivity", erro, e);
                        }catch (Exception e){
                            Log.e("LoginActivity","EXCEPTION", e);
                        }
                        binding.txtErroLogin.setText(erro);
                        binding.txtErroLogin.setVisibility(binding.txtErroLogin.VISIBLE);
                    }
                });
    }
}
