package com.aula.aion.api;

import android.app.Notification;

import com.aula.aion.model.Notificacao;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServiceAPI_NOSQL {
    @GET("/api/v1/notificacao/listar/funcionario/{cdFuncionario}")
    Call<List<Notificacao>> selecionarNotificacaoPorId(@Path("cdFuncionario") Long id);
}
