package com.aula.aion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aula.aion.api.ServiceAPI_CEP;
import com.aula.aion.api.ServiceAPI_SQL;
import com.aula.aion.databinding.ActivityEditarPerfilBinding;
import com.aula.aion.model.ApiCep;
import com.aula.aion.model.Cargo;
import com.aula.aion.model.Funcionario;
import com.aula.aion.model.Endereco;

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
    private Retrofit retrofit;
    private Funcionario funcionario;
    private ActivityEditarPerfilBinding binding;
    private String cep;
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
        binding = ActivityEditarPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.funcionario = (Funcionario) getIntent().getSerializableExtra("funcionario");
        if (funcionario != null) {
            setarInformacoesFuncionario(funcionario);
        }

        buscarEnderecoAtual(funcionario.getCdEndereco());

        Spinner spinner = findViewById(R.id.spinner_estado_civil);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.estado_civil_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        View navBar = findViewById(R.id.editarPerfilNavBar);

        ImageView btnVoltar = navBar.findViewById(R.id.btn_voltar);
        btnVoltar.setOnClickListener(v ->{
            finish();
        });

        binding.txtCep.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String cep = binding.txtCep.getText().toString().trim();
                if (!cep.isEmpty()) {
                    consumirAPICEP(cep);
                }
            }
        });

        binding.txtCep.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // quando perde o foco
                String cep = binding.txtCep.getText().toString().trim();
                if (!cep.isEmpty()) {
                    consumirAPICEP(cep);
                }
            }
        });

        binding.btnSalvar.setOnClickListener(v -> {
            String cep = binding.txtCep.getText().toString().trim();
            String rua = binding.txtRua.getText().toString().trim();
            String complemento = binding.txtComplemento.getText().toString().trim();
            String bairro = binding.txtBairro.getText().toString().trim();
            String cidade = binding.txtCidade.getText().toString().trim();
            String estado = binding.txtEstado.getText().toString().trim();
            int numero = Integer.parseInt(binding.txtNumero.getText().toString().trim());
            Endereco endereco = new Endereco(funcionario.getCdEndereco(), cep, rua, numero, complemento, bairro, cidade, estado);
            if (!this.cep.equals(binding.txtCep.getText().toString())){ // codição se o cep foi alterado
                alterarEndereco(endereco);
            }
            if (funcionario.getEstadoCivil().equals(binding.spinnerEstadoCivil.getSelectedItem().toString())) {
                alterarEstadoCivil(funcionario.getCdMatricula(), binding.spinnerEstadoCivil.getSelectedItem().toString());
            }
            
        });



    }

    private void alterarEstadoCivil(Long cdMatricula, String estadoCivil) {
        
    }

    private void alterarEndereco(Endereco endereco) {
        // Credenciais da API
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("admin", "123456");
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", credentials)
                            .build();
                    return chain.proceed(request);
                })
                .build();
        //Definir a URL da API
        String url = "https://ms-aion-jpa.onrender.com";
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);

        serviceAPI_SQL.alterarEndereco(endereco.getCdEndereco(), endereco).enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if (response.isSuccessful()) {
                    Endereco retorno = response.body();
                    if (retorno != null) {
                        Toast.makeText(EditarPerfil.this, "", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditarPerfil.this, Perfil.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                t.printStackTrace();
                Log.d("chamaAPI_GetByEmail", "Erro na chamada da API: " + t.getMessage());
            }
        });
    }

    private void consumirAPICEP(String cep) {
        //Definir a URL da API
        String url = "https://viacep.com.br";
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceAPI_CEP serviceAPI_CEP = retrofit.create(ServiceAPI_CEP.class);

        serviceAPI_CEP.buscarCep(cep).enqueue(new Callback<ApiCep>() {
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
        binding.txtRua.setText(endereco.getLogradouro());
        binding.txtComplemento.setText(endereco.getComplemento());
        binding.txtBairro.setText(endereco.getBairro());
        binding.txtCidade.setText(endereco.getLocalidade());
        binding.txtEstado.setText(endereco.getUf());
    }
    private void setarInformacoesFuncionario(Funcionario funcionario) {
        DateTimeFormatter formatoOrigem = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dataHora = LocalDateTime.parse(funcionario.getNascimento(), formatoOrigem);
        LocalDate data = dataHora.toLocalDate();

        DateTimeFormatter formatoDestino = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataFormatada = data.format(formatoDestino);

        binding.txtNome.setText(funcionario.getNomeCompleto().toUpperCase());
        binding.txtCpf.setText(funcionario.getCpf());
        binding.txtDataNascimento.setText(dataFormatada);
        setarGenero(funcionario.getSexo());
        setarEstadoCivil(funcionario.getEstadoCivil());

    }
    private void setarGenero(String genero) {

        if (genero.equals("1")) {
            binding.txtGenero.setText("Masculino");
        } else if (genero.equals("2")) {
            binding.txtGenero.setText("Feminino");
        }
        else {
            binding.txtGenero.setText("Outro");
        }
    }
    private void setarEstadoCivil(String estadoCivil) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.estado_civil_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEstadoCivil.setAdapter(adapter);

        int posicao = 0;
        switch (estadoCivil) {
            case "1": posicao = 0; break; // Solteiro(a)
            case "2": posicao = 1; break; // Casado(a)
            case "3": posicao = 2; break; // Divorciado(a)
            case "4": posicao = 3; break; // Viúvo(a)
            case "5": posicao = 4; break; // Separado(a)
        }

        binding.spinnerEstadoCivil.setSelection(posicao);
    }

    private void buscarEnderecoAtual(Long id) {
        // Credenciais da API
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("admin", "123456");
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", credentials)
                            .build();
                    return chain.proceed(request);
                })
                .build();
        //Definir a URL da API
        String url = "https://ms-aion-jpa.onrender.com";
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);

        serviceAPI_SQL.selecionarEnderecoPorId(id).enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if (response.isSuccessful()) {
                    Endereco endereco = response.body();
                    if (endereco != null) {
                        binding.txtCep.setText(endereco.getCep());
                        binding.txtRua.setText(endereco.getRua());
                        binding.txtComplemento.setText(endereco.getComplemento());
                        binding.txtBairro.setText(endereco.getBairro());
                        binding.txtCidade.setText(endereco.getCidade());
                        binding.txtEstado.setText(endereco.getEstado());
                        binding.txtNumero.setText(String.valueOf(endereco.getNumero()));
                        cep = endereco.getCep();
                    }
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}