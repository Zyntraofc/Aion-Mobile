package com.aula.aion.model;

public class TpReclamacao {

    private Long cdTpReclamacao;
    private String nome;

    TpReclamacao() {}
    TpReclamacao(Long cdTpReclamacao, String nome) {
        this.cdTpReclamacao = cdTpReclamacao;
        this.nome = nome;
    }

    public Long getCdTpReclamacao() {
        return cdTpReclamacao;
    }

    public String getNome() {
        return nome;
    }

    public void setCdTpReclamacao(Long cdTpReclamacao) {
        this.cdTpReclamacao = cdTpReclamacao;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
