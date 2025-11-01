package com.aula.aion.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;


public class RelatorioPresenca implements Serializable {
    private LocalDate dataDia;

    @SerializedName("lcdfuncionario")
    private Long lCdFuncionario;

    private Long qtdBatidas;

    private Integer statusDia;

    public RelatorioPresenca(LocalDate dataDia, Long lCdFuncionario, Long qtdBatidas, Integer statusDia) {
        this.dataDia = dataDia;
        this.lCdFuncionario = lCdFuncionario;
        this.qtdBatidas = qtdBatidas;
        this.statusDia = statusDia;
    }

    public RelatorioPresenca(){}

    public Integer getStatusDia() {
        return statusDia;
    }

    public void setStatusDia(Integer statusDia) {
        this.statusDia = statusDia;
    }

    public Long getQtdBatidas() {
        return qtdBatidas;
    }

    public void setQtdBatidas(Long qtdBatidas) {
        this.qtdBatidas = qtdBatidas;
    }

    public Long getlCdFuncionario() {
        return lCdFuncionario;
    }

    public void setlCdFuncionario(Long lCdFuncionario) {
        this.lCdFuncionario = lCdFuncionario;
    }

    public LocalDate getDataDia() {
        return dataDia;
    }

    public void setDataDia(LocalDate dataDia) {
        this.dataDia = dataDia;
    }
}
