package com.aula.aion;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.adapter.JustificativaAdapter;
import com.aula.aion.adapter.NotificacaoAdapter;
import com.aula.aion.databinding.ActivityNotificacaoBinding;
import com.aula.aion.model.Notificacao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eightbitlab.com.blurview.BlurAlgorithm;
import eightbitlab.com.blurview.RenderEffectBlur;
import eightbitlab.com.blurview.RenderScriptBlur;

public class NotificacaoActivity extends AppCompatActivity {

    private ActivityNotificacaoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNotificacaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupBlurView();
        setupListeners();

        RecyclerView recyclerView = binding.recyclerNotificacao;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Notificacao> lista = new ArrayList<>();
        lista.add(new Notificacao("Aion", "Seja bem-vindo ao Aion, sua nova ferramenta de ponto. Qualquer dúvida entre em contato com seu RH responsável.", new Date()));
        lista.add(new Notificacao("Aion", "Há 5 horas!", new Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000)));
        lista.add(new Notificacao("Aion", "Há 2 dias!", new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)));
        lista.add(new Notificacao("Aion", "Há 10 dias!", new Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000)));lista.add(new Notificacao("Aion", "Seja bem-vindo ao Aion, sua nova ferramenta de ponto. Qualquer dúvida entre em contato com seu RH responsável.", new Date()));
        lista.add(new Notificacao("Aion", "Há 5 horas!", new Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000)));
        lista.add(new Notificacao("Aion", "Há 2 dias!", new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)));
        lista.add(new Notificacao("Aion", "Há 10 dias!", new Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000)));lista.add(new Notificacao("Aion", "Seja bem-vindo ao Aion, sua nova ferramenta de ponto. Qualquer dúvida entre em contato com seu RH responsável.", new Date()));
        lista.add(new Notificacao("Aion", "Há 5 horas!", new Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000)));
        lista.add(new Notificacao("Aion", "Há 2 dias!", new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)));
        lista.add(new Notificacao("Aion", "Há 10 dias!", new Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000)));lista.add(new Notificacao("Aion", "Seja bem-vindo ao Aion, sua nova ferramenta de ponto. Qualquer dúvida entre em contato com seu RH responsável.", new Date()));
        lista.add(new Notificacao("Aion", "Há 5 horas!", new Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000)));
        lista.add(new Notificacao("Aion", "Há 2 dias!", new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)));
        lista.add(new Notificacao("Aion", "Há 10 dias!", new Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000)));
        NotificacaoAdapter adapter = new NotificacaoAdapter(lista, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupBlurView() {
        float blurRadius = 11f;
        int overlayColor = Color.parseColor("#86F6F6F6");

        ViewGroup rootView = findViewById(android.R.id.content);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        BlurAlgorithm blurAlgorithm;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurAlgorithm = new RenderEffectBlur();
        } else {
            blurAlgorithm = new RenderScriptBlur(this);
        }

        binding.notificacaoNavBar.blurView
                .setupWith(rootView, blurAlgorithm)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(blurRadius)
                .setOverlayColor(overlayColor)
                .setBlurAutoUpdate(true);
    }
    private void setupListeners() {
        binding.notificacaoNavBar.btnVoltar.setOnClickListener(view -> {
            finish();
        });

    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right);
    }

}