package com.aula.aion.model;

import java.time.LocalDateTime;

public class Notificacao {
    private String cdNotificacao;
    private int cdFuncionario;
    private String descricao;
    private String titulo;
    private LocalDateTime data; // pode ser LocalDateTime se quiser trabalhar com datas

    // Construtor padrão
    public Notificacao() {}

    // Construtor completo
    public Notificacao(String cdNotificacao, int cdFuncionario, String descricao, String titulo, LocalDateTime data) {
        this.cdNotificacao = cdNotificacao;
        this.cdFuncionario = cdFuncionario;
        this.descricao = descricao;
        this.titulo = titulo;
        this.data = data;
    }

    // Getters e Setters
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

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}

