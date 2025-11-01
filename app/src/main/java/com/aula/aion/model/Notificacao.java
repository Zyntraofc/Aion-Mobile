package com.aula.aion.model;


public class Notificacao {
    private String cdNotificacao;
    private int cdFuncionario;
    private String descricao;
    private String titulo;
    private String data;
    private String status;

    public Notificacao() {}

    public Notificacao(String cdNotificacao, int cdFuncionario, String descricao, String titulo, String data, String status) {
        this.cdNotificacao = cdNotificacao;
        this.cdFuncionario = cdFuncionario;
        this.descricao = descricao;
        this.titulo = titulo;
        this.data = data;
        this.status = status;
    }

    public String getCdNotificacao() {
        return cdNotificacao;
    }

    public void setCdNotificacao(String cdNotificacao) {
        this.cdNotificacao = cdNotificacao;
    }

    public int getCdFuncionario() {
        return cdFuncionario;
    }

    public void setCdFuncionario(int cdFuncionario) {
        this.cdFuncionario = cdFuncionario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

