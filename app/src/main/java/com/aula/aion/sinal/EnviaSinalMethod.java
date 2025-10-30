package com.aula.aion.sinal;

import android.util.Log;

import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.model.EnviaSinal;

import java.time.LocalDateTime;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnviaSinalMethod {
    private Retrofit retrofit;
    public void enviaSinal(Long cdUsuario) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("colaborador", "colaboradorpass");
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", credentials)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        String url = "https://ms-aion-jpa.onrender.com";
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EnviaSinal enviaSinal = new EnviaSinal(cdUsuario, "APP", LocalDateTime.now());

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);
        serviceAPI_SQL.enviarSinal(enviaSinal).enqueue(new Callback<EnviaSinal>() {
            @Override
            public void onResponse(Call<EnviaSinal> call, Response<EnviaSinal> response) {
                // Não é necessário lidar com a resposta aqui somente registrar o sinal
                Log.d("enviaSinal", "Sinal enviado com sucesso!");
            }

            @Override
            public void onFailure(Call<EnviaSinal> call, Throwable t) {
                t.printStackTrace();
                Log.d("enviaSinal", "Erro ao enviar o sinal");            }
        });
    }

}
