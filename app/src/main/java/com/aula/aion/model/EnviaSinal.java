package com.aula.aion.model;


public class EnviaSinal {
    private Long cdUsuario;
    private String localUso;
    private String dsinal;

    public EnviaSinal(Long cdUsuario, String localUso, String dsinal) {
        this.cdUsuario = cdUsuario;
        this.localUso = localUso;
        this.dsinal = dsinal;
    }

    public Long getCdUsuario() {
        return cdUsuario;
    }

    public void setCdUsuario(Long cdUsuario) {
        this.cdUsuario = cdUsuario;
    }

    public String getDsinal() {
        return dsinal;
    }

    public void setDsinal(String dsinal) {
        this.dsinal = dsinal;
    }

    public String getLocalUso() {
        return localUso;
    }

    public void setLocalUso(String localUso) {
        this.localUso = localUso;
    }
}
