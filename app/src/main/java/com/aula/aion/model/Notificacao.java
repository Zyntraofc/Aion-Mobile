package com.aula.aion.model;

import java.util.Date;

public class Notificacao {

    private String Remetente;

    private String conteudo;

    private Date data; //Data e hora


    public Notificacao(String Remetente, String conteudo, Date date) {
        this.Remetente = Remetente;
        this.conteudo = conteudo;
        this.data = date;
    }

    public String getRemetente() {
        return Remetente;
    }

    public String getConteudo() {
        return conteudo;
    }

    public Date getData() {
        return data;
    }
}
