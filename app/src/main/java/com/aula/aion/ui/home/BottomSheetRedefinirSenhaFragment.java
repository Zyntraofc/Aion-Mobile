package com.aula.aion.ui.home;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aula.aion.R;
import com.aula.aion.databinding.BottomSheetRedefinirSenhaBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

public class BottomSheetRedefinirSenhaFragment extends BottomSheetDialogFragment {

    private BottomSheetRedefinirSenhaBinding binding;

    public static BottomSheetRedefinirSenhaFragment newInstance() {
        BottomSheetRedefinirSenhaFragment fragment = new BottomSheetRedefinirSenhaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetRedefinirSenhaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnRedefinir.setOnClickListener(v -> {
            String email = binding.inputEmailCorporativo.getText().toString();
            if (!email.isEmpty()) {
                enviarEmailRedefinicaoSenha(email);
            }
            dismiss();
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenBottomSheetDialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void enviarEmailRedefinicaoSenha(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("E-mail de redefinição enviado para: " + email);
                    } else {
                        System.err.println("Erro ao enviar e-mail: " + task.getException());
                    }
                });
    }
}