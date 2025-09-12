package com.aula.aion.model;

import java.time.LocalDateTime;

public class Batida {

    private Long cdBatida;

    private LocalDateTime dataHoraBatida;

    private String justificativa;

    private Long cdFuncionario;

    public Batida(Long cdBatida, LocalDateTime dataHoraBatida, String justificativa, Long cdFuncionario) {
        this.cdBatida = cdBatida;
        this.dataHoraBatida = dataHoraBatida;
        this.justificativa = justificativa;
        this.cdFuncionario = cdFuncionario;
    }

    public Batida(){}

    public Long getCdBatida() {
        return cdBatida;
    }

    public void setCdBatida(Long cdBatida) {
        this.cdBatida = cdBatida;
    }

    public LocalDateTime getDataHoraBatida() {
        return dataHoraBatida;
    }

    public void setDataHoraBatida(LocalDateTime dataHoraBatida) {
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
}
