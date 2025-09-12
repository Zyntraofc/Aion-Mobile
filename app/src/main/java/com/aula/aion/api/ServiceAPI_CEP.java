package com.aula.aion.api;

import com.aula.aion.model.ApiCep;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServiceAPI_CEP {
    @GET("/ws/{cep}/json/")
    Call<ApiCep> buscarCep(@Path("cep") String cep);
}
