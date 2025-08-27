package com.aula.aion.ui.home;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.aula.aion.R;
import com.aula.aion.databinding.BottomSheetReclamacaoBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BottomSheetReclamacaoFragment extends BottomSheetDialogFragment  {

    BottomSheetReclamacaoBinding binding;

    public BottomSheetReclamacaoFragment() {}

    public static BottomSheetReclamacaoFragment newInstance() {
        BottomSheetReclamacaoFragment fragment = new BottomSheetReclamacaoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenBottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetReclamacaoBinding.inflate(inflater, container, false);

        binding.txtData.setText(String.format("%s", new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date())));

        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);

        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true); // Evita colapso parcial
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa o binding para evitar memory leaks
    }
}