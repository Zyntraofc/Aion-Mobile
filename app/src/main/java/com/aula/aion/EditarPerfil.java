package com.aula.aion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.aula.aion.model.Funcionario;
import com.aula.aion.model.Endereco;
import com.google.gson.Gson;

import java.time.LocalDate;
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
    private String cepAtual = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityEditarPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.funcionario = (Funcionario) getIntent().getSerializableExtra("funcionario");
        if (funcionario != null) {
            setarInformacoesFuncionario(funcionario);
        }

        buscarEnderecoAtual(funcionario.getCdEndereco());

        habilitarCamposEdicao();

        View navBar = findViewById(R.id.editarPerfilNavBar);
        ImageView btnVoltar = navBar.findViewById(R.id.btn_voltar);
        btnVoltar.setOnClickListener(v -> finish());

        binding.txtCep.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String cep = binding.txtCep.getText().toString().trim().replace("-", "");
                if (!cep.isEmpty() && cep.length() == 8) {
                    consumirAPICEP(cep);
                }
            }
        });

        binding.btnSalvar.setOnClickListener(v -> salvarAlteracoes());

        binding.txtCep.addTextChangedListener(new TextWatcher() {
            boolean isUpdating = false;
            String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString().replaceAll("[^\\d]", ""); // só dígitos

                if (isUpdating) {
                    oldText = str;
                    isUpdating = false;
                    return;
                }

                StringBuilder formatted = new StringBuilder();
                int len = str.length();

                if (len > 5) {
                    formatted.append(str.substring(0, 5))
                            .append('-')
                            .append(str.substring(5, Math.min(8, len)));
                } else {
                    formatted.append(str);
                }

                isUpdating = true;
                binding.txtCep.setText(formatted.toString());
                binding.txtCep.setSelection(binding.txtCep.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    private void habilitarCamposEdicao() {
        binding.txtCep.setEnabled(true);
        binding.txtNumero.setEnabled(true);
        binding.txtComplemento.setEnabled(true);
    }

    private void salvarAlteracoes() {
        try {
            if (!validarCampos()) {
                return;
            }

            String cep = binding.txtCep.getText().toString();
            String rua = binding.txtRua.getText().toString().trim();
            String complemento = binding.txtComplemento.getText().toString().trim();
            String bairro = binding.txtBairro.getText().toString().trim();
            String cidade = binding.txtCidade.getText().toString().trim();
            String estado = binding.txtEstado.getText().toString().trim();

            Integer numero = Integer.parseInt(binding.txtNumero.getText().toString().trim());

            Endereco endereco = new Endereco(null, cep, rua, numero, complemento, bairro, cidade, estado);

            boolean alteracoesEndereco = !cepAtual.equals(endereco.getCep()) ||
                    !binding.txtRua.getText().toString().equals(binding.txtRua.getTag() == null ? "" : binding.txtRua.getTag().toString()) ||
                    !binding.txtBairro.getText().toString().equals(binding.txtBairro.getTag() == null ? "" : binding.txtBairro.getTag().toString()) ||
                    !binding.txtCidade.getText().toString().equals(binding.txtCidade.getTag() == null ? "" : binding.txtCidade.getTag().toString()) ||
                    !binding.txtEstado.getText().toString().equals(binding.txtEstado.getTag() == null ? "" : binding.txtEstado.getTag().toString()) ||
                    numero != (binding.txtNumero.getTag() == null ? 0 : (int) binding.txtNumero.getTag()) ||
                    !complemento.equals(binding.txtComplemento.getTag() == null ? "" : binding.txtComplemento.getTag().toString());

            if (alteracoesEndereco) {
                alterarEndereco(endereco);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Número inválido", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCampos() {
        String cep = binding.txtCep.getText().toString().trim();
        String numero = binding.txtNumero.getText().toString().trim();

        if (cep.isEmpty()) {
            binding.txtCep.setError("CEP é obrigatório");
            return false;
        }

        if (numero.isEmpty()) {
            binding.txtNumero.setError("Número é obrigatório");
            return false;
        }

        try {
            Integer.parseInt(numero);
        } catch (NumberFormatException e) {
            binding.txtNumero.setError("Número inválido");
            return false;
        }

        return true;
    }

    private void alterarEndereco(Endereco endereco) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("admin", "123456");
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

        ServiceAPI_SQL serviceAPI_SQL = retrofit.create(ServiceAPI_SQL.class);
        Log.d("API_Endereco", "Alterando endereco: " + funcionario.getCdEndereco()+ "  " + endereco.getCep());
        Log.d("EnderecoUpdate", "Response JSON: " + new Gson().toJson(endereco));
        serviceAPI_SQL.alterarEndereco(funcionario.getCdEndereco(), endereco).enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                Log.d("API_Endereco", "Resposta: " + response.body());
                Log.d("EnderecoUpdate", "Response JSON: " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    Endereco retorno = response.body();
                    if (retorno != null) {
                        Toast.makeText(EditarPerfil.this, "Dados alterados com sucesso", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EditarPerfil.this, Perfil.class);
                        intent.putExtra("funcionario", funcionario);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                t.printStackTrace();
                Log.e("API_Endereco", "Erro: " + t.getMessage());
                Toast.makeText(EditarPerfil.this, "Falha na comunicação", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consumirAPICEP(String cep) {
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
                    if (retorno != null) {
                        setarInformacoesEndereco(retorno);
                    } else {
                        Toast.makeText(EditarPerfil.this, "CEP não encontrado", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiCep> call, Throwable t) {
                Log.e("API_CEP", "Erro: " + t.getMessage());
                Toast.makeText(EditarPerfil.this, "Erro ao buscar CEP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setarInformacoesEndereco(ApiCep endereco) {
        binding.txtRua.setText(endereco.getLogradouro());
        binding.txtComplemento.setText(endereco.getComplemento());
        binding.txtBairro.setText(endereco.getBairro());
        binding.txtCidade.setText(endereco.getLocalidade());
        binding.txtEstado.setText(endereco.getUf());

        binding.txtRua.setTag(endereco.getLogradouro());
        binding.txtBairro.setTag(endereco.getBairro());
        binding.txtCidade.setTag(endereco.getLocalidade());
        binding.txtEstado.setTag(endereco.getUf());
    }

    private void setarInformacoesFuncionario(Funcionario funcionario) {
        DateTimeFormatter formatoOrigem = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate data = LocalDate.parse(funcionario.getNascimento(), formatoOrigem);

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
        } else {
            binding.txtGenero.setText("Outro");
        }
    }

    private void setarEstadoCivil(String estadoCivil) {
        String estado = "";

        if (estadoCivil.equals("1")) {
            estado = "Solteiro(a)";
        } else if (estadoCivil.equals("2")) {
            estado = "Casado(a)";
        } else if (estadoCivil.equals("3")) {
            estado = "Divorciado(a)";
        } else if (estadoCivil.equals("4")) {
            estado = "Viúvo(a)";
        } else if (estadoCivil.equals("5")) {
            estado = "Separado(a)";
        } else {
            estado = "Não informado";
        }

        binding.txtEstadoCivil.setText(estado);
    }

    private void buscarEnderecoAtual(Long id) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic("admin", "123456");
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

                        cepAtual = endereco.getCep();

                        binding.txtRua.setTag(endereco.getRua());
                        binding.txtBairro.setTag(endereco.getBairro());
                        binding.txtCidade.setTag(endereco.getCidade());
                        binding.txtEstado.setTag(endereco.getEstado());
                        binding.txtNumero.setTag(endereco.getNumero());
                        binding.txtComplemento.setTag(endereco.getComplemento());
                    }
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                Log.e("API_Endereco", "Erro ao buscar endereço: " + t.getMessage());
                Toast.makeText(EditarPerfil.this, "Erro ao carregar endereço", Toast.LENGTH_SHORT).show();
            }
        });
    }
}