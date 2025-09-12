package com.aula.aion.api;

import com.aula.aion.model.Batida;
import com.aula.aion.model.Cargo;
import com.aula.aion.model.Endereco;
import com.aula.aion.model.Funcionario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServiceAPI_SQL {

    @GET("/api/v1/funcionario/buscar/email/{email}")
    Call<Funcionario> selecionarFuncionarioPorEmail(@Path("email") String email);

    @GET("/api/v1/cargo/buscar/{id}")
    Call<Cargo> selecionarCargoPorId(@Path("id") Long id);

    @POST("/api/v1/batida/inserir")
    Call<Batida> inserirBatida(@Body Batida batida);

    @GET("/api/v1/endereco/buscar/{id}")
    Call<Endereco> selecionarEnderecoPorId(@Path("id") Long id);

    @PUT("/api/v1/endereco/alterar/{id}")
    Call<Endereco> alterarEndereco(@Path("id") Long id, @Body Endereco endereco);
}
