package com.aula.aion.model;

import java.time.LocalDateTime;

public class EnviaSinal {
    private Long cdUsuario;
    private String localUso;
    private LocalDateTime dsinal;

    public EnviaSinal(Long cdUsuario, String localUso, LocalDateTime dsinal) {
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

    public LocalDateTime getDsinal() {
        return dsinal;
    }

    public void setDsinal(LocalDateTime dsinal) {
        this.dsinal = dsinal;
    }

    public String getLocalUso() {
        return localUso;
    }

    public void setLocalUso(String localUso) {
        this.localUso = localUso;
    }
}
