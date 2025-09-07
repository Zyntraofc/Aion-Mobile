package com.aula.aion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.model.ApiCep;
import com.aula.aion.model.Cargo;
import com.aula.aion.model.Funcionario;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditarPerfil extends AppCompatActivity {
    Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Spinner spinner = findViewById(R.id.spinner_estado_civil);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.estado_civil_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        View navBar = findViewById(R.id.editarPerfilNavBar);

        ImageView btnSalvar = navBar.findViewById(R.id.btn_voltar);
        btnSalvar.setOnClickListener(v ->{
            Intent intent = new Intent(EditarPerfil.this, Perfil.class);
            startActivity(intent);
            finish();
        });

        Funcionario funcionario = (Funcionario) getIntent().getSerializableExtra("funcionario");
        if (funcionario != null) {
            setarInformacoesFuncionario(funcionario);
        }

        TextInputEditText txtCep = findViewById(R.id.txt_cep);

        txtCep.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String cep = txtCep.getText().toString().trim();
                if (!cep.isEmpty()) {
                    consumirAPICEP(cep);
                }
            }
        });

        txtCep.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // quando perde o foco
                String cep = txtCep.getText().toString().trim();
                if (!cep.isEmpty()) {
                    consumirAPICEP(cep);
                }
            }
        });

        TextInputEditText txtNumero = findViewById(R.id.txt_numero);



    }
    private void consumirAPICEP(String cep) {
        //Definir a URL da API
        String url = "https://viacep.com.br";
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);

        serviceAPI_SQL.buscarCep(cep).enqueue(new Callback<ApiCep>() {
            @Override
            public void onResponse(Call<ApiCep> call, Response<ApiCep> response) {
                if (response.isSuccessful()) {
                    ApiCep retorno = response.body();
                    setarInformacoesEndereco(retorno);
                }
            }

            @Override
            public void onFailure(Call<ApiCep> call, Throwable t) {
                t.printStackTrace();
                Log.d("Erro API CEP", "Erro na chamada da API: " + t.getMessage());
            }
        });
    }
    private void setarInformacoesEndereco(ApiCep endereco) {

        TextInputEditText txtRua = findViewById(R.id.txt_rua);
        TextInputEditText txtComplemento = findViewById(R.id.txt_complemento);
        TextInputEditText txtBairro = findViewById(R.id.txt_bairro);

        txtRua.setText(endereco.getLogradouro());
        txtComplemento.setText(endereco.getComplemento());
        txtBairro.setText(endereco.getBairro());
    }
    private void setarInformacoesFuncionario(Funcionario funcionario) {

        TextInputEditText txtNome = findViewById(R.id.txt_ome);
        TextInputEditText txtCpf = findViewById(R.id.txt_cpf);
        TextInputEditText txtDataNascimento = findViewById(R.id.txt_data_nascimento);

        DateTimeFormatter formatoOrigem = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dataHora = LocalDateTime.parse(funcionario.getNascimento(), formatoOrigem);
        LocalDate data = dataHora.toLocalDate();

        DateTimeFormatter formatoDestino = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataFormatada = data.format(formatoDestino);

        txtNome.setText(funcionario.getNomeCompleto().toUpperCase());
        txtCpf.setText(funcionario.getCpf());
        txtDataNascimento.setText(dataFormatada);
        setarGenero(funcionario.getSexo());
        setarEstadoCivil(funcionario.getEstadoCivil());

    }
    private void setarGenero(String genero) {
        TextInputEditText txtGenero = findViewById(R.id.txt_genero);

        if (genero.equals("1")) {
            txtGenero.setText("Masculino");
        } else if (genero.equals("2")) {
            txtGenero.setText("Feminino");
        }
        else {
            txtGenero.setText("Outro");
        }
    }
    private void setarEstadoCivil(String estadoCivil) {
        Spinner spinner = findViewById(R.id.spinner_estado_civil);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.estado_civil_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int posicao = 0;
        switch (estadoCivil) {
            case "1": posicao = 0; break; // Solteiro(a)
            case "2": posicao = 1; break; // Casado(a)
            case "3": posicao = 2; break; // Divorciado(a)
            case "4": posicao = 3; break; // Viúvo(a)
        }

        spinner.setSelection(posicao);
    }

}