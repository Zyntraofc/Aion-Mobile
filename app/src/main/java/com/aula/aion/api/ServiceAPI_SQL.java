package com.aula.aion.api;

import com.aula.aion.model.Batida;
import com.aula.aion.model.Cargo;
import com.aula.aion.model.CountResponse;
import com.aula.aion.model.Endereco;
import com.aula.aion.model.EnviaSinal;
import com.aula.aion.model.Funcionario;
import com.aula.aion.model.MotivoFalta;
import com.aula.aion.model.Reclamacao;
import com.aula.aion.model.RelatorioPresenca;
import com.aula.aion.model.TpReclamacao;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServiceAPI_SQL {

    @GET("/api/v1/funcionario/buscar/email/{email}")
    Call<Funcionario> selecionarFuncionarioPorEmail(@Path("email") String email);

    @GET("/api/v1/funcionario/relatorio-presencas/{cdMatricula}")
    Call<List<RelatorioPresenca>> listarRelatorioPresenca(@Path("cdMatricula") Long cdMatricula);

    @GET("/api/v1/cargo/buscar/{id}")
    Call<Cargo> selecionarCargoPorId(@Path("id") Long id);

    @GET("/api/v1/reclamacao/buscar/funcionario/{id}")
    Call<List<Reclamacao>> selecionarReclamacaoPorFuncionario(@Path("id") Long id);

    @GET("/api/v1/tpReclamacao/listar")
    Call<List<TpReclamacao>> listarTpReclamacao();

    @GET("/api/v1/tpReclamacao/buscar/{id}")
    Call<TpReclamacao> selecionarTpReclamacaoPorId(@Path("id") Long id);

    @GET("/api/v1/endereco/buscar/{id}")
    Call<Endereco> selecionarEnderecoPorId(@Path("id") Long id);

    @GET("/api/v1/motivoFalta/listar")
    Call<List<MotivoFalta>> listarMotivoFalta();

    @GET("/api/v1/batida/count/{id}")
    Call<CountResponse> countJustificativa(@Path("id") Long id);

    @POST("/api/v1/batida/inserir")
    Call<String> inserirBatida(@Body Batida batida);

    @POST("/api/v1/reclamacao/inserir")
    Call<Reclamacao> inserirReclamacao(@Body Reclamacao reclamacao);

    @PUT("/api/v1/endereco/atualizar/{id}")
    Call<Endereco> alterarEndereco(@Path("id") Long id, @Body Endereco endereco);

    @POST("/api/v1/funcionario/enviar-sinal")
    Call<ResponseBody> enviarSinal(@Body EnviaSinal enviaSinal);

}
