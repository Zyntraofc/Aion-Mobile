package com.aula.aion.model;

public class Reclamacao {
    private final String classsificacao;
    private final String descricao;
    private final String data;

    public Reclamacao(String classsificacao, String descricao, String data) {
        this.classsificacao = classsificacao;
        this.descricao = descricao;
        this.data = data;
    }

    public String getClasssificacao() {
        return classsificacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getData() {
        return data;
    }
}
