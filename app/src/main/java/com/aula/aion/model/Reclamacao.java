package com.aula.aion.model;

import java.time.LocalDate;

public class Reclamacao {

    private Long cdReclamacao;

    private LocalDate reclamacao;

    private String descricao;

    private Long cdFuncionario;

    private Long cdTpReclamacao;

    private String status;

    public Reclamacao() {}

    public Reclamacao(Long cdReclamacao, LocalDate reclamacao, String descricao, Long cdFuncionario, Long cdTpReclamacao, String status) {
        this.cdReclamacao = cdReclamacao;
        this.reclamacao = reclamacao;
        this.descricao = descricao;
        this.cdFuncionario = cdFuncionario;
        this.cdTpReclamacao = cdTpReclamacao;
        this.status = status;
    }

    public Long getCdReclamacao() {
        return cdReclamacao;
    }

    public void setCdReclamacao(Long cdReclamacao) {
        this.cdReclamacao = cdReclamacao;
    }

    public LocalDate getReclamacao() {
        return reclamacao;
    }

    public void setReclamacao(LocalDate reclamacao) {
        this.reclamacao = reclamacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getCdFuncionario() {
        return cdFuncionario;
    }

    public void setCdFuncionario(Long cdFuncionario) {
        this.cdFuncionario = cdFuncionario;
    }

    public Long getCdTpReclamacao() {
        return cdTpReclamacao;
    }

    public void setCdTpReclamacao(Long cdTpReclamacao) {
        this.cdTpReclamacao = cdTpReclamacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
