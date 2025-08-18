package com.aula.aion.model;

import com.aula.aion.adapter.JustificativaAdapter;

//adptar (Vinicius ABS)
public class Justificativa {
    private final String titulo;
    private final String descricao;
    private final String data;

    public Justificativa(String titulo, String descricao, String data) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
    }
    public Justificativa(String titulo, String data) {
        this.titulo = titulo;
        this.data = data;
        this.descricao = null;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getData() {
        return data;
    }
}
