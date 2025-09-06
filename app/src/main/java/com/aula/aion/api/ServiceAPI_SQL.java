package com.aula.aion.api;

import com.aula.aion.model.Cargo;
import com.aula.aion.model.Funcionario;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServiceAPI_SQL {

    @GET("/api/funcionario/selecionar/email/{email}")
    Call<Funcionario> selecionarFuncionarioPorEmail(@Path("email") String email);

    @GET("/api/cargo/selecionar/{id}")
    Call<Cargo> selecionarCargoPorId(@Path("id") Long id);

}
