package com.aula.aion.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.aula.aion.Inicio;
import com.aula.aion.R;
import com.aula.aion.sinal.EnviaSinalMethod;

public class DashboardsFragments extends Fragment {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboards, container, false);

        webView = view.findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebViewClient(new WebViewClient());

        Inicio activity = (Inicio) getActivity();
        if (activity != null) {
            Long cdMatricula = activity.getFuncionario().getCdMatricula();
            EnviaSinalMethod enviaSinalMethod = new EnviaSinalMethod();
            enviaSinalMethod.enviaSinal(cdMatricula);
            String powerBiUrl = "https://app.powerbi.com/view?r=eyJrIjoiYzBjY2U1Y2ItMzdkYy00ZTBlLTkzNDEtNWM0N2JlNDk5MTAwIiwidCI6ImIxNDhmMTRjLTIzOTctNDAyYy1hYjZhLTFiNDcxMTE3N2FjMCJ9&filter=public_ids_funcionario%2Flcdmatricula%20eq%20" + cdMatricula;
            Log.d("URL", powerBiUrl);
            webView.loadUrl(powerBiUrl);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.destroy();
        }
    }
}