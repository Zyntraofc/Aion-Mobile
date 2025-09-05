package com.aula.aion.model;

public class Cargo {
    private Long cdCargo;

    private String nome;

    private String cargoConfianca;

    private String ativo;

    public Cargo() {}

    public Cargo(Long cdCargo, String nome, String cargoConfianca, String ativo) {
        this.cdCargo = cdCargo;
        this.nome = nome;
        this.cargoConfianca = cargoConfianca;
        this.ativo = ativo;
    }

    public Long getCdCargo() {
        return cdCargo;
    }

    public void setCdCargo(Long cdCargo) {
        this.cdCargo = cdCargo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCargoConfianca() {
        return cargoConfianca;
    }

    public void setCargoConfianca(String cargoConfianca) {
        this.cargoConfianca = cargoConfianca;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }
}
