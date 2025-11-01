package com.aula.aion.api;

import com.aula.aion.model.Notificacao;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface ServiceAPI_NOSQL {
    @GET("/api/v1/notificacao/listar/funcionario/{cdFuncionario}")
    Call<List<Notificacao>> selecionarNotificacaoPorId(@Path("cdFuncionario") Long id);

    @PATCH("api/v1/notificacao/atualizar/status/{cdNotificacao}/{status}")
    Call<Notificacao> atualizarNotificacao(@Path("cdNotificacao") String cdNotificacao, @Path("status") String status);

    @GET("/api/v1/notificacao/contar/{cdNotificacao}/{status}")
    Call<Integer> contarNotificacao(@Path("cdNotificacao") Long cdFuncionario, @Path("status") String status);
}
