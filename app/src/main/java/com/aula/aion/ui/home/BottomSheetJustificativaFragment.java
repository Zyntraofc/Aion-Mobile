package com.aula.aion.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aula.aion.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.aula.aion.databinding.BottomSheetAbsenceBinding; // Ajuste o nome conforme seu binding

public class BottomSheetJustificativaFragment extends BottomSheetDialogFragment {

    private BottomSheetAbsenceBinding binding; // Binding gerado a partir do layout

    public static BottomSheetJustificativaFragment newInstance() {
        BottomSheetJustificativaFragment fragment = new BottomSheetJustificativaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAbsenceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenBottomSheetDialog);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recuperar os argumentos passados
        Bundle args = getArguments();
        if (args != null) {
            String data = args.getString("data", "Dia 06/06");
            binding.txtData.setText(data);
        }

        // Configurar o BottomSheetBehavior para expandir totalmente
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true); // Evita colapso parcial
        }

        // Configurar o botão de justificativa
        binding.btnJustificar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Justificativa enviada para " + (args != null ? args.getString("data") : "data inválida"), Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa o binding para evitar memory leaks
    }
}