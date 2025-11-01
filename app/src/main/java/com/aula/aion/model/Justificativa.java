package com.aula.aion.model;

public class Justificativa {
    private String data;
    private int numInclusao;

    public Justificativa(String data, int numInclusao) {
        this.data = data;
        this.numInclusao = numInclusao;
    }


    public String getData() {
        return data;
    }

    public int getNumInclusao() {
        return numInclusao;
    }
}