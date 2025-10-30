package com.aula.aion.model;

import java.time.LocalDateTime;

public class Batida {
    private String dataHoraBatida;
    private String justificativa;
    private Long cdFuncionario;
    private String status;
    private String situacao;
    private Long cdMotivoFalta;


    public Batida(String dataHoraBatida, String justificativa, Long cdFuncionario, String status, String situacao, Long cdMotivo) {
        this.dataHoraBatida = dataHoraBatida;
        this.justificativa = justificativa;
        this.cdFuncionario = cdFuncionario;
        this.status = status;
        this.situacao = situacao;
        this.cdMotivoFalta = cdMotivo;
    }

    public String getDataHoraBatida() {
        return dataHoraBatida;
    }

    public void setDataHoraBatida(String dataHoraBatida) {
        this.dataHoraBatida = dataHoraBatida;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public Long getCdFuncionario() {
        return cdFuncionario;
    }

    public void setCdFuncionario(Long cdFuncionario) {
        this.cdFuncionario = cdFuncionario;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Long getCdMotivoFalta() {
        return cdMotivoFalta;
    }

    public void setCdMotivoFalta(Long cdMotivoFalta) {
        this.cdMotivoFalta = cdMotivoFalta;
    }
}
