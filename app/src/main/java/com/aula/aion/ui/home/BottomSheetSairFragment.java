package com.aula.aion.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aula.aion.Login;
import com.aula.aion.LogoutCallback;
import com.aula.aion.R;
import com.aula.aion.databinding.BottomSheetSairBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

public class BottomSheetSairFragment extends BottomSheetDialogFragment {

    BottomSheetSairBinding binding;
    private LogoutCallback logoutCallback; // Callback para notificar a decisão
    public static BottomSheetSairFragment newInstance() {
        BottomSheetSairFragment fragment = new BottomSheetSairFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = BottomSheetSairBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenBottomSheetDialog);
    }

    // Método para definir o callback
    public void setLogoutCallback(LogoutCallback callback) {
        this.logoutCallback = callback;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCancelar.setOnClickListener(v -> {
            if (logoutCallback != null) {
                logoutCallback.onLogoutDecision(false);
            }
            dismiss();
        });

        binding.btnSair.setOnClickListener(v -> {
            if (logoutCallback != null) {
                logoutCallback.onLogoutDecision(true);
                FirebaseAuth.getInstance().signOut();
            }
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
