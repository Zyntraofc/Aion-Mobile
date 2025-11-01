package com.aula.aion.sinal;

import android.util.Log;

import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.model.EnviaSinal;

import java.time.LocalDateTime;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
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

        String url = "https://ms-aion-jpa.onrender.com/";

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String dataAtual = LocalDateTime.now().toString();
        EnviaSinal enviaSinal = new EnviaSinal(cdUsuario, "mobile", dataAtual);

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);
        serviceAPI_SQL.enviarSinal(enviaSinal).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body() != null ? response.body().string() : "vazio";
                        Log.d("enviaSinal", "Sinal enviado com sucesso: " + body);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.e("enviaSinal", "Erro: " + response.code() + " - " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e("enviaSinal", "Falha ao enviar sinal: " + t.getMessage());
            }
        });

    }
}
