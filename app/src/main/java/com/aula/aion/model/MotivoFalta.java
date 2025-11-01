package com.aula.aion.model;

public class MotivoFalta {
    private Long cdMotivoFalta;
    private String motivoFalta;
    private Long cdEmpresa;

    public MotivoFalta() {}

    public MotivoFalta(Long cdMotivoFalta, String motivoFalta, Long cdEmpresa) {
        this.cdMotivoFalta = cdMotivoFalta;
        this.motivoFalta = motivoFalta;
        this.cdEmpresa = cdEmpresa;
    }

    public Long getCdMotivoFalta() {
        return cdMotivoFalta;
    }

    public void setCdMotivoFalta(Long cdMotivoFalta) {
        this.cdMotivoFalta = cdMotivoFalta;
    }

    public String getMotivoFalta() {
        return motivoFalta;
    }

    public void setMotivoFalta(String motivoFalta) {
        this.motivoFalta = motivoFalta;
    }

    public Long getCdEmpresa() {
        return cdEmpresa;
    }

    public void setCdEmpresa(Long cdEmpresa) {
        this.cdEmpresa = cdEmpresa;
    }

    @Override
    public String toString() {
        return motivoFalta;
    }
}
