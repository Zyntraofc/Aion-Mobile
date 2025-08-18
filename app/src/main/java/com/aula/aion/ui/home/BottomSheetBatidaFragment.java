package com.aula.aion.ui.home;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aula.aion.R;
import com.aula.aion.databinding.BottomSheetBatidaBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.core.content.ContextCompat;
import android.text.style.ForegroundColorSpan;

public class BottomSheetBatidaFragment extends BottomSheetDialogFragment {

    private BottomSheetBatidaBinding binding;

    public static BottomSheetBatidaFragment newInstance() {
        BottomSheetBatidaFragment fragment = new BottomSheetBatidaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetBatidaBinding.inflate(inflater, container, false);
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
        binding.btnBaterPonto.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Justificativa enviada para " + (args != null ? args.getString("data") : "data inválida"), Toast.LENGTH_SHORT).show();
            dismiss();
        });

        // Configurar o botão Confirmar
        binding.btnBaterPonto.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Ponto confirmado para " + (args != null ? args.getString("data") : "data inválida"), Toast.LENGTH_SHORT).show();
            dismiss();
        });

        // Configurar o SpannableString para o texto clicável
        SpannableString spannableString = new SpannableString("Não quer confirmar? Toque aqui");

        // Define a parte "Toque aqui" como clicável e azul
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                dismiss();
            }
        };

        int startIndex = spannableString.toString().indexOf("Toque aqui");
        int endIndex = startIndex + "Toque aqui".length();
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.txtLink.setText(spannableString);
        binding.txtLink.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        binding.txtLink.setHighlightColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}