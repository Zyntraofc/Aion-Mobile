package com.aula.aion.model;


import java.io.Serializable;

public class Funcionario implements Serializable {

    private Long cdMatricula;

    private String nomeCompleto;

    private String cpf;

    private String rg;

    private String nascimento; // ver de deixar em data

    private String estadoCivil;

    private String admissao;  // ver de deixar em data

    private Long cdGestor;

    private Integer dependentes;

    private String email;

    private String hashSenha;

    private String telefone;

    private String sexo;

    private Long cdDepartamento;

    private Long cdCargo;

    public Funcionario() {}

    public Funcionario(Long cdMatricula, String nomeCompleto, String cpf, String rg, String nascimento,
            String estadoCivil, String admissao, Long cdGestor, Integer dependentes, String email,
            String hashSenha, String telefone, String sexo, Long cdDepartamento, Long cdCargo) {
        this.cdMatricula = cdMatricula;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.rg = rg;
        this.nascimento = nascimento;
        this.estadoCivil = estadoCivil;
        this.admissao = admissao;
        this.cdGestor = cdGestor;
        this.dependentes = dependentes;
        this.email = email;
        this.hashSenha = hashSenha;
        this.telefone = telefone;
        this.sexo = sexo;
        this.cdDepartamento = cdDepartamento;
        this.cdCargo = cdCargo;
    }

    public Long getCdMatricula() {
        return cdMatricula;
    }

    public void setCdMatricula(Long cdMatricula) {
        this.cdMatricula = cdMatricula;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Long getCdCargo() {
        return cdCargo;
    }

    public void setCdCargo(Long cdCargo) {
        this.cdCargo = cdCargo;
    }

    public Long getCdDepartamento() {
        return cdDepartamento;
    }

    public void setCdDepartamento(Long cdDepartamento) {
        this.cdDepartamento = cdDepartamento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getHashSenha() {
        return hashSenha;
    }

    public void setHashSenha(String hashSenha) {
        this.hashSenha = hashSenha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDependentes() {
        return dependentes;
    }

    public void setDependentes(Integer dependentes) {
        this.dependentes = dependentes;
    }

    public Long getCdGestor() {
        return cdGestor;
    }

    public void setCdGestor(Long cdGestor) {
        this.cdGestor = cdGestor;
    }

    public String getAdmissao() {
        return admissao;
    }

    public void setAdmissao(String admissao) {
        this.admissao = admissao;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }
}
